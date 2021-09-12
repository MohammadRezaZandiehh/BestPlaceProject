package com.example.bestplaceproject

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.bestplaceproject.databinding.ActivityAddPlaceBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import ir.hamsaa.persiandatepicker.Listener
import ir.hamsaa.persiandatepicker.PersianDatePickerDialog
import ir.hamsaa.persiandatepicker.util.PersianCalendar
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class AddPlaceActivity : AppCompatActivity() {

    //lateinit yani badan mikham initialize esh konam .     yani init esho b tavigh bendaz .
    lateinit var binding: ActivityAddPlaceBinding
    var imageAddressLocation: String? = null
    private var id: String? = null
    private var bestPlaceSelectedToEdit: BestPlace? = null


    companion object {
        const val TAG = "tag"
        const val REQUEST_CODE_CAMERA = 13134
        const val REQUEST_CODE_GALLERY = 13135
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPlaceBinding.inflate(LayoutInflater.from(this@AddPlaceActivity))
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

//        binding.edtDate!!.showSoftInputOnFocus = false
        binding.edtDate.setShowSoftInputOnFocus(false);
        //khodesh khate bala ro gozashte bood.
        binding.edtDate.setOnFocusChangeListener { v, hasFocus ->

            if (!hasFocus) return@setOnFocusChangeListener

            showDatePiker()
        }




        if (intent.extras?.getString("id") != null) {
            id = intent.extras?.getString("id")
            val db = DatabaseHelper(this)
            val bestPlace = db.getPlaceById(id!!)
            bestPlaceSelectedToEdit = bestPlace

            binding.edtTittle.setText(bestPlace.title)
            binding.edtDescription.setText(bestPlace.description)
            binding.edtDate.setText(bestPlace.data)
            binding.edtLocation.setText(bestPlace.location)

            var imageBitmap = MediaStore.Images.Media.getBitmap(
                contentResolver, Uri.fromFile(
                    File(bestPlace.image)
                )
            )
            binding.imageViewSelectedPicture.setImageBitmap(imageBitmap)
        }


        binding.buttonSelectedImage.setOnClickListener {

            AlertDialog.Builder(this@AddPlaceActivity)
                .setTitle("انتخاب عکس")
                .setItems(
                    arrayOf(
                        "انتخاب عکس از گالری",
                        "گرفتن عکس با دوربین"
                    )
                ) { dialog, options ->
                    when (options) {
                        0 -> {
                            choosePhotoFromGallery()
                        }
                        1 -> {
                            choosePhotoByCamera()
                        }
                    }
                }
                .show()
        }




        binding.buttonAddPlace.setOnClickListener {

            var title = binding.edtTittle.text.toString()
            var description = binding.edtDescription.text.toString()
            var date = binding.edtDate.text.toString()
            var location = binding.edtLocation.text.toString()

            // if e zir : agar karbar raft toooye AddPlaceActivity va khast edit kone chizi o vali aks ro edit nakrd , aksemoon hamon ghablie bemoone .
            if (imageAddressLocation == null) {
                imageAddressLocation = bestPlaceSelectedToEdit?.image
            }
            val myBestPlace =
                BestPlace(title, imageAddressLocation!!, description, date, location, 0.0, 0.0)


// dar bala id ro initial kardim va hala inja migim motmaenim khali nisti pas bia id e object e bestPlace e sakhte shode ro barabar e "" hamoon id ii k bahash omadim too in activity gharar midim "" :
            id?.let {
                myBestPlace.id = it
            }


            val db = DatabaseHelper(this)
            db.addPlace(myBestPlace)

            //bbin inja (dar khate bala) omade hame ye etelaat e vared shode ro b database moon ham add karde va karemoon tamoom shode
            //pas miaym bad az ejrash AddPlaceActivity ro mibandim va result ro OK gharar midim :

            setResult(Activity.RESULT_OK)
            finish()
        }
    }


    private fun showDatePiker() {
        val picker = PersianDatePickerDialog(this)
            .setPositiveButtonString("باشه")
            .setNegativeButton("بیخیال")
            .setTodayButton("برو به امروز")
            .setTodayButtonVisible(true)
            .setMinYear(1300)
            .setMaxYear(PersianDatePickerDialog.THIS_YEAR)
            .setActionTextColor(Color.GRAY)
            .setTitleType(PersianDatePickerDialog.WEEKDAY_DAY_MONTH_YEAR)
            .setShowInBottomSheet(true)
            .setListener(object : Listener {
                override fun onDateSelected(persianCalendar: PersianCalendar) {
                    binding.edtDate.setText(persianCalendar?.persianLongDate?.toString())
                }

                override fun onDismissed() {
                    Toast.makeText(
                        this@AddPlaceActivity,
                        "باید یک تاریخ انتخاب کنید",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })

        picker.show()
    }


    private fun choosePhotoFromGallery() {
        Dexter.withContext(this@AddPlaceActivity)
            .withPermissions(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()) {

                        capturePictureFromGallery()

                    }

                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    showRationalPermission()
                }

            })
            .check()
    }


    private fun choosePhotoByCamera() {
        Dexter.withContext(this@AddPlaceActivity)
            .withPermissions(
                android.Manifest.permission.CAMERA
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()) {

                        capturePictureByCamera()

                    }

                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    showRationalPermission()
                }

            })
            .check()
    }


    private fun capturePictureFromGallery() {
        var galleryIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, REQUEST_CODE_GALLERY)
    }


    private fun capturePictureByCamera() {
        var cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        //khate bala mire b doorbin ta aks begire .
        startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA)
        //REQUEST_CODE_CAMERA ye sabetie k vase methode startAtivityForResult bayad bezarim .
    }


    private fun showRationalPermission() {
        AlertDialog.Builder(this@AddPlaceActivity)
            .setTitle("اجازه دسترسی به فایل ها دوربین")
            .setPositiveButton("برو به تنظیمات") { _, _ ->
                val settingIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                //khate bala khoe system amel ma ro mibare b ghesmate tanzimat .
                val uri = Uri.fromParts("package", packageName, null)
                //khate bala ye uri b ma mide k uri ei hast k manzoore  ma hast bere onja . chejoori? ba dastore packageName i k beyne 2 ta virgoolebala gharar dadam migarde bbine kodom package name HAM NAME package name aslie mast .
                settingIntent.data = uri
                startActivity(settingIntent)
            }
            .setNegativeButton("اجازه نمیدهم") { _, _ ->
                finish()
            }
            .show()
    }


    private fun saveImageOnInternalStorage(bitmap: Bitmap): String {
        //khate paein : file ro sakhtim ba addrese test.jpg
//bestPlaceSelectedToEdit?.id : dakhele {}e zir khodm ino gozashtm ta ba id taghir kone .
        var fileAndPath = buildFile("test ${System.currentTimeMillis()}.jpg")
        try {
            var portal = buildOutputStream(fileAndPath)
            //khate bala gharar dadane file dar portal .

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, portal)
            // az bitmap k ye seri adad hast ye image misaze va az portal obooresh mide .
            //khate bala daghighan jaei  hast k on adamak az portal oboor mikone va zaaaaakhire mishe . . . dar vaghe akharin kare mast .


            portal.flush()
            //khate bala :har chi dakhele khodet hast ro befrest
            portal.close()
        } catch (ex: IOException) {
            ex.printStackTrace()
        }

        return fileAndPath.absolutePath
        //khate bala addrese akse zakhire shode ro dar ghalebe ye String b ma barmigardoone .

    }


    // method e paein portal ya hamoon darvaze ye voroodi ro misaze (mese hamon shekli k khodesh tozih dad)
    fun buildOutputStream(file: File): FileOutputStream {
        return FileOutputStream(file)
    }


    // method e paein khode file ro vasamoon misaze (hamoon adamke too sheklesh)
    fun buildFile(fileName: String): File {
        //this.fileDir dare b Internal Storage e app e ma eshare mikone . . . yani hamon jaei k file haye mokhtase har app toosh zakhire mishe .
//        khate paein : ye file b esme fileNme dar Internal Storage zakhire mikone . yani masalan agar esme file bashe jamshid.mp4 --> miad too data/package name/jamshid.mp4  zakhire mikone .
        return File(this.filesDir, fileName)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAMERA) {
                var bitmapImage = data?.extras?.get("data") as Bitmap
                //methode bala daghighan hamchin shekli kar mikone k ghablan bahash kar mikardim : putExtra("username", "AmirAhmad")
                //Bitmap yani zakhire aks b soorate --> number .

                imageAddressLocation = saveImageOnInternalStorage(bitmapImage)

                binding.imageViewSelectedPicture.setImageBitmap(bitmapImage)
            }
        }

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_GALLERY) {
                if (data != null) {
                    var contentUri = data.data //uri : addres e aks
                    try {
                        var imageBitmap =
                            MediaStore.Images.Media.getBitmap(this.contentResolver, contentUri)

                        imageAddressLocation = saveImageOnInternalStorage(imageBitmap)
                        binding.imageViewSelectedPicture.setImageBitmap(imageBitmap)
                    } catch (ex: FileNotFoundException) {
                        Toast.makeText(this, "فایل شما پیدا نشد !", Toast.LENGTH_SHORT).show()


                    }

                }
            }
        }


        if (resultCode == Activity.RESULT_CANCELED) {
            if (requestCode == REQUEST_CODE_CAMERA) {
                //cancel
                Toast.makeText(this, "عکسی نگرفتید !", Toast.LENGTH_SHORT).show()

            }
            if (requestCode == REQUEST_CODE_GALLERY) {
                //cancel
                Toast.makeText(this, "فایلی انتخاب نشد !", Toast.LENGTH_SHORT).show()

            }
        }
    }


    //har moghe karbar rooye back click kone in activity sda zade mishe :
    override fun onBackPressed() {
        super.onBackPressed()
        setResult(Activity.RESULT_CANCELED)
    }
}