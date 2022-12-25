package com.mgchoi.smartportfolio

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.gson.Gson
import com.mgchoi.smartportfolio.databinding.ActivityReceiveBinding
import com.mgchoi.smartportfolio.db.MemberDAO
import com.mgchoi.smartportfolio.db.PortfolioDAO
import com.mgchoi.smartportfolio.model.Member
import com.mgchoi.smartportfolio.model.Portfolio
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class ReceiveActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReceiveBinding

    private var bluetoothManager: BluetoothManager? = null
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var device: BluetoothDevice? = null
    private var acceptSocket: BluetoothServerSocket? = null
    private var socket: BluetoothSocket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 닫기 버튼
        binding.cardReceiveClose.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // 연결 버튼
        binding.btnReceiveBt.setOnClickListener {
            checkBluetooth()
        }
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
                handleReceive()
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
                handleReceive()
            } else {
                // 활성화하지 않았을 시 에러 메시지 표시
                Toast.makeText(this, R.string.share_bt_off_message, Toast.LENGTH_LONG).show()
                onBackPressedDispatcher.onBackPressed()
            }
        }

    private fun handleReceive() {
        // 기기를 연결할 것을 요구하는 알림 생성
        AlertDialog.Builder(this).apply {
            setTitle(R.string.share_bt_bond)
            setMessage(R.string.share_bt_bond_message_for_client)
            setNegativeButton(R.string.connect) { _, _ ->
                // 블루투스 연결할 수 있도록 이동
                val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
                btConnectLauncher.launch(intent)
            }
            setPositiveButton(R.string.share_bt_standby) { _, _ ->
                // 바로 연결 대기
                checkBluetoothConnectPermission()
            }
            show()
        }
    }

    private val btConnectLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
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
            standbyForConnection()
        } else {
            // 권한 요청
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // API 31 이상은 권한 요청
                permissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT)
            } else {
                // 이하는 바로 기기 선택
                standbyForConnection()
            }
        }
    }

    private val permissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            if (it) {
                standbyForConnection()
            } else {
                // 권한 거절 에러 메시지 출력
                Toast.makeText(this, R.string.share_bt_permission, Toast.LENGTH_LONG).show()
            }
        }

    @SuppressLint("MissingPermission")
    private fun standbyForConnection() {
        bluetoothAdapter?.let { adapter ->
            // 서버 소켓으로 연결 요청 대기
            acceptSocket = adapter.listenUsingInsecureRfcommWithServiceRecord(
                "SmartPortfolio",
                ShareActivity.BT_UUID
            )

            CoroutineScope(Dispatchers.IO).launch {
                while (true) {
                    socket = try {
                        acceptSocket?.accept()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        break
                    }

                    // 연결되면 데이터 수신 작업 진행
                    if (socket != null) {
                        receiveData()
                        break
                    }
                }
            }

        }
    }

    private fun receiveData() {
        CoroutineScope(Dispatchers.IO).launch {
            var len: Int
            val buffer = ByteArray(1024)
            val os = ByteArrayOutputStream()

            while (true) {
                try {
                    socket?.let {
                        len = it.inputStream.read(buffer)
                        val data = buffer.copyOf(len)
                        os.write(data)

                        // 데이터를 받으면
                        if (it.inputStream.available() == 0) {
                            val dataByteArr = os.toByteArray()
                            val stringData = String(dataByteArr)

                            // OutputStream 비우기
                            os.reset()

                            withContext(Dispatchers.Main) {
                                // 데이터 수신 완료 알림
                                AlertDialog.Builder(this@ReceiveActivity).apply {
                                    setTitle(R.string.share_receive_success)
                                    setMessage(R.string.share_receive_success_message)
                                    setPositiveButton(R.string.confirm) { d, _ ->
                                        saveTransferredData(stringData)
                                        onBackPressedDispatcher.onBackPressed()
                                        d.dismiss()
                                    }
                                    setNegativeButton(R.string.no) { d, _ ->
                                        onBackPressedDispatcher.onBackPressed()
                                        d.dismiss()
                                    }
                                    show()
                                }
                            }
                        }

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    break
                }
            }
        }
    }

    private fun saveTransferredData(json: String) {
        // 전송받은 데이터 저장

        // Array<String> 으로 변환
        val strArray = Gson().fromJson(json, Array<String>::class.java)

        var member: Member
        val portfolios: Array<Portfolio>
        try {
            // array[0]: Member, array[1]: Portfolio
            val memberJson = strArray[0]
            val portfolioJson = strArray[1]

            // JSON을 Array로 변환
            member = Gson().fromJson(memberJson, Member::class.java)
            portfolios = Gson().fromJson(portfolioJson, Array<Portfolio>::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, R.string.unknown_error_message, Toast.LENGTH_LONG).show()
            return
        }

        // Member DB에 import
        val memberDao = MemberDAO(this)
        memberDao.insert(member)
        member = memberDao.selectLast()

        // Portfolio DB에 import
        val portfolioDao = PortfolioDAO(this)
        for (p in portfolios) {
            p.memberId = member.id
            portfolioDao.insert(p)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        // 연결 종료 및 메모리 비우기
        socket?.close()
        socket = null

        acceptSocket?.close()
        acceptSocket = null
    }
}