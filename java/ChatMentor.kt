package com.example.buzzhub
import android.app.Activity
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.akexorcist.screenshotdetection.ScreenshotDetectionDelegate
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.storage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.ArrayList
import java.util.UUID
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants

class ChatMentor : AppCompatActivity(), ScreenshotDetectionDelegate.ScreenshotDetectionListener {

    companion object {
        private const val REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION = 3009
    }

    var builder = NotificationCompat.Builder(this, "MyNotifications")
        .setContentTitle("Screen Shot")
        .setSmallIcon(R.drawable.ic_launcher_background)
        .setAutoCancel(true)
        .setContentText("Screenshot Taken")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)




    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_screen)
        checkReadExternalStoragePermission()
        createNotificationChannel()



        var imageUri: Uri? = null
        var chatList = ArrayList<MessageData>()
        var topic = ""
        var firebaseUser: FirebaseUser? = null
        var reference: DatabaseReference? = null
        var chatRecyclerView = findViewById<RecyclerView>(R.id.chatRecyclerView)

        val recieverid = intent.getStringExtra("recieverid")
        firebaseUser = Firebase.auth.currentUser
        val userId = firebaseUser!!.uid

        var name1: String = intent.getStringExtra("recievername").toString()
        var profileImage1: String = intent.getStringExtra("recieverimage").toString()





        fun readMessage(senderId: String, receiverId: String) {
            val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Chat")

            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Log.d("ReadingError", "Error: ${error.message}") // Log the error message
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        Log.d("SnapshotEmpty", "No data found in snapshot")
                        return
                    }
                    chatList.clear()
                    for (dataSnapshot: DataSnapshot in snapshot.children) {
                        val messageText = dataSnapshot.child("message").getValue(String::class.java)
                        val senderUserId = dataSnapshot.child("senderId").getValue(String::class.java)
                        val receiverUserId = dataSnapshot.child("receiverId").getValue(String::class.java)
                        val timestamp = dataSnapshot.child("time").getValue(String::class.java)

                        if ((senderUserId == senderId && receiverUserId == receiverId) ||
                            (senderUserId == receiverId && receiverUserId == senderId)
                        ) {
                            val chat = MessageData(senderUserId, receiverUserId, messageText.toString(), timestamp.toString())
                            chatList.add(chat)
                        } else {
                            Log.d("Message in chat mentor function", "No message")
                        }
                    }

                    val chatAdapter = ChatAdapter(this@ChatMentor, chatList)
                    chatRecyclerView.adapter = chatAdapter
                    chatRecyclerView.layoutManager = LinearLayoutManager(this@ChatMentor)
                }
            })
        }

        readMessage(firebaseUser!!.uid , recieverid.toString())

        Log.d("RecieverIDrecieved", recieverid.toString())
        var recieverData: chatscreenData = chatscreenData("","","", "")

        reference = FirebaseDatabase.getInstance().getReference("USER").child(userId!!)

        val db = Firebase.database.reference.child("USER").child(recieverid.toString())
        db.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val name = snapshot.child("name").getValue(String::class.java)
                val time = snapshot.child("time").getValue(String::class.java)

                val profileImage = snapshot.child("profileImage").getValue(String::class.java)
                recieverData = chatscreenData(
                    recieverid.toString(),
                    name1,
                    profileImage1,
                    time.toString()

                )

                val nameofreciever: TextView = findViewById(R.id.name)
                Log.d("RecieverName", recieverData.name)
                Log.d("RecieverID", recieverData.id)
                Log.d("RecieverProfileImage", recieverData.profileImage)
                //Toast.makeText(this, recieverData.name, Toast.LENGTH_SHORT).show()
                nameofreciever.text = name1
                val profileImage2: CircleImageView = findViewById(R.id.profileima)
                Glide.with(this).load(profileImage1).into(profileImage2)


            }

        }


        fun sendMessage(senderId: String, receiverId: String, message: String, time : String) {
            val reference: DatabaseReference? = FirebaseDatabase.getInstance().getReference()

            val hashMap: HashMap<String, String> = HashMap()
            hashMap.put("senderId", senderId)
            hashMap.put("receiverId", receiverId)
            hashMap.put("message", message)
            hashMap.put("time", time)

            reference!!.child("Chat").push().setValue(hashMap)
            readMessage(senderId, receiverId)

        }

        val send: ImageView = findViewById(R.id.sendbutton)
        val text: TextView = findViewById(R.id.textmessage)
        send.setOnClickListener{
            var message: String = text.text.toString()

            if (message.isEmpty()) {
                Toast.makeText(applicationContext, "Message is empty", Toast.LENGTH_SHORT).show()
                text.setText("")
            } else {

                val currentTimeMillis = System.currentTimeMillis()



                sendMessage(firebaseUser!!.uid, recieverid.toString(), message, currentTimeMillis.toString() )
                text.setText("")

                topic = "/topics/$userId"
                /*PushNotification(NotificationData( userName!!,message),
                    topic).also {
                    sendNotification(it)}*/
            }

        }

        var downloadUrl: String? = null // Declare downloadUrl variable outside the scope

        // Function to upload image to Firebase Storage
        fun uploadImageToFirebaseStorage(imageUri: Uri) {
            val filename = UUID.randomUUID().toString()
            val ref = com.google.firebase.Firebase.storage.getReference("/images/$filename")
            ref.putFile(imageUri)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { url ->
                        // Assign the downloadUrl value
                        downloadUrl = url.toString()
                        val time = System.currentTimeMillis().toString()
                        sendMessage(firebaseUser!!.uid, recieverid.toString(), downloadUrl.toString(), time)
                        // Once the image is uploaded, call the function to push mentor data to the database

                    }
                }
                .addOnFailureListener { exception ->
                    // Handle failure
                    Log.e(ContentValues.TAG, "Error uploading image: ${exception.message}")

                }
        }


        val pickImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri = result.data?.data

                // Check if imageUri is not null
                imageUri?.let {
                    // Upload image to Firebase Storage
                    uploadImageToFirebaseStorage(it)
                }
            }
        }



        val gallerybtn : ImageView = findViewById(R.id.imagebutton)
        gallerybtn.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImage.launch(galleryIntent)
        }

        fun saveimage(bitmap: Bitmap){
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray = stream.toByteArray()
            val filename = "image.png"
            val file = File(this.filesDir, filename)
            file.createNewFile()
            val fileOutputStream = FileOutputStream(file)
            fileOutputStream.write(byteArray)
            fileOutputStream.close()
            val uri = Uri.fromFile(file)
            imageUri = uri
            uploadImageToFirebaseStorage(uri)
        }

        val cameraActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val imageBitmap = data?.extras?.get("data") as Bitmap
                val imageView = findViewById<ImageView>(R.id.messageimage)
                imageView.setImageBitmap(imageBitmap)
                saveimage(imageBitmap)
            } else {
                // Handle case when user cancels capturing image
            }
        }

// Set click listener for camera button
        val cam: ImageView = findViewById(R.id.camerabutton)
        cam.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraActivityResult.launch(cameraIntent)
        }

        val pickVideoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val videoUri: Uri? = result.data?.data

                // Check if videoUri is not null
                videoUri?.let { uri ->
                    val filename = UUID.randomUUID().toString()
                    val storageRef = Firebase.storage.reference.child("videos/$filename")

                    // Upload video to Firebase Storage
                    storageRef.putFile(uri)
                        .addOnSuccessListener { taskSnapshot ->
                            // Get the download URL for the uploaded video
                            taskSnapshot.storage.downloadUrl.addOnSuccessListener { downloadUrl ->
                                val time = System.currentTimeMillis().toString()


                                // Send a message with the video download URL
                                sendMessage(userId, recieverid.toString(), downloadUrl.toString(), time)
                            }.addOnFailureListener { exception ->
                                // Handle failure to get download URL
                                Log.e(TAG, "Error getting download URL: ${exception.message}")
                            }
                        }
                        .addOnFailureListener { exception ->
                            // Handle failure to upload video
                            Log.e(TAG, "Error uploading video: ${exception.message}")
                        }
                }
            }
        }


        val back: ImageView = findViewById(R.id.backbutton)
        back.setOnClickListener{
            val intent = Intent(this, Feed_Info::class.java)
            startActivity(intent)
        }

        val voice : ImageView = findViewById(R.id.callbutton)
        voice.setOnClickListener{
            /*val options = ChannelMediaOptions()
            options.channelProfile= Constants.CHANNEL_PROFILE_COMMUNICATION
            options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
            agoraEngine!!.start*/
            val intent = Intent(this, AudioCall::class.java)
            startActivity(intent)
        }

        val video : ImageView = findViewById(R.id.videocallbutton)
        video.setOnClickListener{
            val intent = Intent(this, VideoCall::class.java)
            startActivity(intent)
        }



    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "MyNotifications"
            val descriptionText = "This is a notification channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("MyNotifications", "First cahnnel", importance).apply {
                description = "Testing"
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    private val screenshotDetectionDelegate = ScreenshotDetectionDelegate(this, this)



    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (!hasFocus) {

            with(NotificationManagerCompat.from(this)) {
                if (ActivityCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {

                    return
                }
                notify(1, builder.build())
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION -> {
                if (grantResults.getOrNull(0) == PackageManager.PERMISSION_DENIED) {
                    showReadExternalStoragePermissionDeniedMessage()
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun checkReadExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestReadExternalStoragePermission()
        }
    }

    private fun requestReadExternalStoragePermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION)
    }

    private fun showReadExternalStoragePermissionDeniedMessage() {
        Toast.makeText(this, "Read external storage permission has denied", Toast.LENGTH_SHORT).show()
    }

    override fun onStart() {
        super.onStart()
        screenshotDetectionDelegate.startScreenshotDetection()
    }

    override fun onStop() {
        super.onStop()
        screenshotDetectionDelegate.stopScreenshotDetection()
    }


    override fun onScreenCaptured(path: String) {
        Toast.makeText(this, "Screenshot captured: $path", Toast.LENGTH_SHORT).show()
    }

    override fun onScreenCapturedWithDeniedPermission() {

    }
}