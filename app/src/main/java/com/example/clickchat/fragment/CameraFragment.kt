import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.hardware.camera2.*
import android.media.Image
import android.media.ImageReader
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.clickchat.FindUsersActivity
import com.example.clickchat.R
import com.example.clickchat.ShowCaptureActivity
import com.example.clickchat.loginRegistration.SplashScreenActivity
import com.google.firebase.auth.FirebaseAuth
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

class CameraFragment : Fragment() {

    private lateinit var cameraId: String
    private lateinit var cameraManager: CameraManager
    private var cameraDevice: CameraDevice? = null
    private var captureSession: CameraCaptureSession? = null
    private lateinit var captureRequestBuilder: CaptureRequest.Builder
    private lateinit var imageReader: ImageReader
    private lateinit var mSurfaceView: SurfaceView
    private lateinit var mSurfaceHolder: SurfaceHolder

    private val cameraOpenCloseLock = Semaphore(1)

    private lateinit var backgroundThread: HandlerThread
    private lateinit var backgroundHandler: Handler

    private val TAG = "CameraFragment"

    companion object {
        private const val CAMERA_REQUEST_CODE = 1

        fun newInstance(): CameraFragment {
            return CameraFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_camera, container, false)

        mSurfaceView = view.findViewById(R.id.surfaceView)
        mSurfaceHolder = mSurfaceView.holder

        cameraManager = requireActivity().getSystemService(Context.CAMERA_SERVICE) as CameraManager

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_REQUEST_CODE
            )
        } else {
            startBackgroundThread()
            mSurfaceHolder.addCallback(object : SurfaceHolder.Callback {
                override fun surfaceCreated(holder: SurfaceHolder) {
                    openCamera()
                }

                override fun surfaceChanged(
                    holder: SurfaceHolder,
                    format: Int,
                    width: Int,
                    height: Int
                ) {
                }

                override fun surfaceDestroyed(holder: SurfaceHolder) {
                    closeCamera()
                    stopBackgroundThread()
                }
            })
        }

        val mLogout = view.findViewById<Button>(R.id.logout)
        val mCapture = view.findViewById<ImageButton>(R.id.capture)
        val mFindUsers = view.findViewById<Button>(R.id.findUsers)
        mLogout.setOnClickListener { logOut() }
        mCapture.setOnClickListener { captureImage() }
        mFindUsers.setOnClickListener { findUsers() }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        closeCamera()
        stopBackgroundThread()
    }

    private fun startBackgroundThread() {
        backgroundThread = HandlerThread("CameraBackground").also { it.start() }
        backgroundHandler = Handler(backgroundThread.looper)
    }

    private fun stopBackgroundThread() {
        backgroundThread.quitSafely()
        try {
            backgroundThread.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun openCamera() {
        try {
            if (!cameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw RuntimeException("Time out waiting to lock camera opening.")
            }
            cameraManager.cameraIdList.firstOrNull { id ->
                val characteristics = cameraManager.getCameraCharacteristics(id)
                characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK
            }?.let { id ->
                cameraId = id
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    cameraManager.openCamera(cameraId, stateCallback, backgroundHandler)
                }
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun closeCamera() {
        try {
            cameraOpenCloseLock.acquire()
            captureSession?.close()
            captureSession = null
            cameraDevice?.close()
            cameraDevice = null
            if (::imageReader.isInitialized) {
                imageReader.close()
            }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } finally {
            cameraOpenCloseLock.release()
        }
    }

    private val stateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            Log.d(TAG, "Camera opened")
            cameraOpenCloseLock.release()
            cameraDevice = camera
            createCameraPreviewSession()
        }

        override fun onDisconnected(camera: CameraDevice) {
            Log.d(TAG, "Camera disconnected")
            cameraOpenCloseLock.release()
            camera.close()
            cameraDevice = null
        }

        override fun onError(camera: CameraDevice, error: Int) {
            Log.e(TAG, "Camera error: $error")
            cameraOpenCloseLock.release()
            camera.close()
            cameraDevice = null
            activity?.finish()
        }
    }

    private fun createCameraPreviewSession() {
        try {
            val surface = mSurfaceHolder.surface

            imageReader = ImageReader.newInstance(
                mSurfaceView.width,
                mSurfaceView.height,
                ImageFormat.JPEG,
                1
            ).apply {
                setOnImageAvailableListener({ reader ->
                    backgroundHandler.post(ImageSaver(reader.acquireNextImage()))
                }, backgroundHandler)
            }

            captureRequestBuilder =
                cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW).apply {
                    addTarget(surface)
                }

            val surfaces = mutableListOf<Surface>().apply {
                add(surface)
                add(imageReader.surface)
            }

            cameraDevice!!.createCaptureSession(
                surfaces,
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        if (cameraDevice == null) return

                        captureSession = session
                        try {
                            captureRequestBuilder.set(
                                CaptureRequest.CONTROL_AF_MODE,
                                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                            )
                            captureSession!!.setRepeatingRequest(
                                captureRequestBuilder.build(),
                                null,
                                backgroundHandler
                            )
                            Log.d(TAG, "Camera preview session created")
                        } catch (e: CameraAccessException) {
                            e.printStackTrace()
                        }
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
                    }
                },
                backgroundHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun captureImage() {
        try {
            if (cameraDevice == null || captureSession == null) {
                Log.e(TAG, "CameraDevice or CaptureSession is null")
                return
            }
            val captureBuilder =
                cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE).apply {
                    addTarget(imageReader.surface)
                    set(
                        CaptureRequest.CONTROL_AF_MODE,
                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                    )
                }

            captureSession!!.stopRepeating()
            captureSession!!.capture(
                captureBuilder.build(),
                object : CameraCaptureSession.CaptureCallback() {
                    override fun onCaptureCompleted(
                        session: CameraCaptureSession,
                        request: CaptureRequest,
                        result: TotalCaptureResult
                    ) {
                        Toast.makeText(requireContext(), "Image Captured", Toast.LENGTH_SHORT)
                            .show()
                        createCameraPreviewSession()
                    }
                },
                backgroundHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            Log.e(TAG, "CameraDevice was already closed")
        }
    }

    private inner class ImageSaver(private val image: Image) : Runnable {
        override fun run() {
            val buffer = image.planes[0].buffer
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes)

            val decodedBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            val rotatedBitmap = rotate(decodedBitmap)
            val fileLocation = saveImageToStorage(rotatedBitmap)

            if (fileLocation != null) {
                val intent = Intent(activity, ShowCaptureActivity::class.java)
                intent.putExtra("imagePath", fileLocation)
                startActivity(intent)
            }

            image.close()
        }

        private fun rotate(bitmap: Bitmap): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(90f)
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }

        private fun saveImageToStorage(bitmap: Bitmap): String? {
            val file = File(requireActivity().externalMediaDirs.first(), "imageToSend.jpg")
            return try {
                val stream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                stream.flush()
                stream.close()
                file.absolutePath
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }
    }

    private fun logOut() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(context, SplashScreenActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun findUsers() {
        val intent = Intent(context, FindUsersActivity::class.java)
        startActivity(intent)
    }
}
