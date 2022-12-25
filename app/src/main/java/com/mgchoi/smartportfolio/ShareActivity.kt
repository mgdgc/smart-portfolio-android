package com.mgchoi.smartportfolio

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.mgchoi.smartportfolio.databinding.ActivityShareBinding
import com.mgchoi.smartportfolio.db.MemberDAO
import com.mgchoi.smartportfolio.db.PortfolioDAO
import com.mgchoi.smartportfolio.model.Member
import com.mgchoi.smartportfolio.model.Portfolio
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class ShareActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_MEMBER_ID = "memberId"
        val BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }

    private lateinit var binding: ActivityShareBinding

    private var bluetoothManager: BluetoothManager? = null
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var device: BluetoothDevice? = null
    private var socket: BluetoothSocket? = null

    private lateinit var member: Member
    private var portfolios: ArrayList<Portfolio> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShareBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarShare)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initIntentData()

        // 연결 버튼 클릭
        binding.btnShareConnect.setOnClickListener {
            checkBluetooth()
        }

        // 전송 버튼 클릭
        binding.cardShareBtn.setOnClickListener {
            transfer()
        }

    }

    @SuppressLint("SetTextI18n")
    private fun initIntentData() {
        val memberId = intent?.getIntExtra(EXTRA_MEMBER_ID, -1) ?: return
        if (memberId < 0) return

        // Member Data 가져오기
        val memberDAO = MemberDAO(this)
        memberDAO.select(memberId)?.let {
            member = it
        } ?: // Member를 성공적으로 가져오지 못했다면 종료
        run {
            // Member를 성공적으로 가져오지 못했다면 종료
            Toast.makeText(this, R.string.unknown_error_message, Toast.LENGTH_LONG).show()
            // Member를 성공적으로 가져오지 못했다면 종료
            onBackPressedDispatcher.onBackPressed()
        }

        // Portfolio Data 가져오기
        val portfolioDAO = PortfolioDAO(this)
        this.portfolios.addAll(portfolioDAO.selectAll(memberId))

        // 전송할 데이터 표시
        binding.txtShareDataInfo.text = """
            ${getString(R.string.share_data_member)}: ${member.name}
            ${getString(R.string.share_data_count)}: ${portfolios.size}
        """.trimIndent()
    }

    private fun checkBluetooth() {
        bluetoothManager = getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager?.adapter

        // 블루투스를 지원하지 않는 경우
        if (bluetoothAdapter == null) {
            // 블루투스를 지원하지 않는다는 에러 메시지 표시
            AlertDialog.Builder(this).apply {
                setTitle(R.string.share_bt_not_supported)
                setMessage(R.string.share_bt_not_supported_message)
                setPositiveButton(R.string.close) { _, _ -> onBackPressedDispatcher.onBackPressed() }
                show()
            }
        }

        // 블루투스를 켜지 않은 경우
        bluetoothAdapter?.let {
            if (!it.isEnabled) {
                // 블루투스를 켜달라는 에러 메시지 표시
                AlertDialog.Builder(this).apply {
                    setTitle(R.string.share_bt_not_supported)
                    setMessage(R.string.share_bt_not_supported_message)
                    setPositiveButton(R.string.enable) { _, _ ->
                        // 블루투스 켜기
                        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                        bluetoothLauncher.launch(intent)
                    }
                    show()
                }
            } else {
                // 블루투스가 켜져 있으면 공유 작업 계속
                checkBluetoothConnectPermission()
            }
        }
    }

    private val bluetoothLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            // 블루투스 켬 요청 수행
            if (it.resultCode == Activity.RESULT_OK) {
                // 블루투스가 켜지면 공유 작업 계속
                checkBluetoothConnectPermission()
            } else {
                // 활성화하지 않았을 시 에러 메시지 표시
                Toast.makeText(this, R.string.share_bt_off_message, Toast.LENGTH_LONG).show()
            }
        }

    private val btConnectLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            // 블루투스 연결을 마쳤다면 장치 선택으로 진행
            checkBluetoothConnectPermission()
        }

    private fun checkBluetoothConnectPermission() {
        // 블루투스 권한 확인
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // 권한 허용 상태일 시 기기 선택
            selectDevice()
        } else {
            // 권한 요청
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // API 31 이상은 권한 요청
                permissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT)
            } else {
                // 이하는 바로 기기 선택
                selectDevice()
            }
        }
    }

    private val permissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            if (it) {
                // 기기 선택
                selectDevice()
            } else {
                // 활성화하지 않았을 시 에러 메시지 표시
                Toast.makeText(this, R.string.share_bt_permission, Toast.LENGTH_LONG).show()
            }
        }

    @SuppressLint("MissingPermission")
    private fun selectDevice() {
        bluetoothAdapter?.let { adapter ->
            // 연결된 모든 기기 가져오기
            val devices = adapter.bondedDevices
            val arrayList = arrayListOf<BluetoothDevice>()
            for (device in devices) {
                arrayList.add(device)
                Log.d("device", device.name)
            }
            val items = Array<String>(arrayList.size) { arrayList[it].name }

            // 기기 선택창
            val builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.share_bt_bond)
                .setItems(items) { d, i ->
                    // 선택한 기기와 연결
                    this.device = arrayList[i]
                    connectDevice()
                }
                .setNeutralButton(R.string.share_bt_connect) { _, _ ->
                    // 블루투스 기기를 연결할 수 있도록 이동
                    val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
                    btConnectLauncher.launch(intent)
                }
                .setNegativeButton(R.string.cancel) { d, _ -> d.dismiss() }
                .show()

        }
    }

    @SuppressLint("MissingPermission")
    private fun connectDevice() {
        device?.let { device ->
            try {
                // 소켓 통신 연결
                socket = device.createRfcommSocketToServiceRecord(BT_UUID)
                socket?.connect()

                // 연결된 기기 표시
                binding.txtShareDevice.text = device.name

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, R.string.share_bt_connect_error, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun transfer() {
        socket?.let { socket ->
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // OutputStream으로 데이터 전송
                    val os = socket.outputStream
                    val data = generateDataJson().toByteArray()
                    withContext(Dispatchers.IO) {
                        os.write(data)
                        os.flush()
                    }

                    Log.d("data", generateDataJson())
                    Log.d("data", String(data))

                    withContext(Dispatchers.Main) {
                        // 데이터 전송 완료 알림
                        AlertDialog.Builder(this@ShareActivity).apply {
                            setTitle(R.string.share_transfer_success)
                            setMessage(R.string.share_transfer_success_message)
                            setPositiveButton(R.string.confirm) { d, _ ->
                                onBackPressedDispatcher.onBackPressed()
                                d.dismiss()
                            }
                            show()
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        // 전송 도중 발생한 오류 알림
                        Toast.makeText(
                            this@ShareActivity,
                            R.string.share_bt_send_fail,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

        } ?: run {
            // 기기가 연결되지 않았다면 먼저 기기를 연결할 것을 요구
            Snackbar.make(binding.root, R.string.share_bt_connect_error, Snackbar.LENGTH_LONG)
                .setAction(R.string.connect) {
                    checkBluetooth()
                }
                .show()
        }
    }

    private fun generateDataJson(): String {
        // Export할 수 있도록 JSON으로 변환
        val memberJson = Gson().toJson(member)
        val portfolioJson = Gson().toJson(portfolios)
        // 하나의 JSON으로 묶기
        val grouped = arrayOf(memberJson, portfolioJson)
        return Gson().toJson(grouped)
    }

    override fun onSupportNavigateUp(): Boolean {
        super.onSupportNavigateUp()
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        // 연결 종료 및 메모리 비우기
        socket?.close()
        socket = null
    }
}