package com.example.jreader

/**
 * Android Project 시작 2019.06.19
 * Project 주제: 텍스트 리더
 */

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.annotation.RequiresApi
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.Reader

class MainActivity : AppCompatActivity() {
    private val permissionsRequestCode = 123
    private lateinit var managePermissions: ManagePermissions

    private var listAdapter: ArrayAdapter<String>? = null
    private var items = ArrayList<String>()

    // 루트 경로 가져오기
    private val rootPath: String = Environment.getExternalStorageDirectory().absolutePath
    var prevPath = ""
    var nextPath = ""
    var currentPath = rootPath

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 권한 확인 리스트
        val list = listOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        // 새로운 ManagePermissions class 인스턴스 생성
        managePermissions = ManagePermissions(this, list, permissionsRequestCode)

        // 권한 상태 확인
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            managePermissions.checkPermissions()
        }
    }

    // 초기화
    private fun init(): Boolean {
        Log.d("디버그", "초기화")

        // 파일 객체 생성
        val fileRoot = File(rootPath)

        // 파일 루트가 폴더가 아닐경우
        if (!fileRoot.isDirectory) {
            toast("폴더가 아닙니다.")
            return false
        }

        // 파일 리스트 가져오기
        val fileList = fileRoot.list()
        if (fileList === null) {
            toast("파일 목록이 존재하지 않습니다.")
            return false
        }

        // 아이템 리스트 전부 삭제
        items.clear()

        // 리스트의 첫 항목은 뒤로가기 위해 ".." 세팅
        items.add("..")
        fileList.forEach { file ->
            Log.d("파일", file.toString())
            items.add(file)
        }

        // 리스트 어댑터
        listAdapter = ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, items)
        // 리스트 뷰
        listView.adapter = listAdapter

        listView.setOnItemClickListener { parent, view, position, id ->
            Log.d("디버그", "리스트 뷰 클릭")
            var path = items.get(position)
            if (path == "..") {
                prevPath()
            } else {
                nextPath(path)
            }
        }

        // 리스트 뷰에 적용
        listAdapter?.notifyDataSetChanged()

        return true
    }

    private fun prevPath() {
        // 마지막 /의 위치 찾기
        var lastSlashPosition = currentPath.lastIndexOf("/")

        // 처음부터 마지막 / 까지의 문자열 가져오기
        prevPath = currentPath.substring(0, lastSlashPosition)

        // 최상위 권 까지 올라온 상태이다.
        if (prevPath == "/storage/emulated") {
            toast("상위 폴더가 존재하지 않습니다.")
            return
        }

        Log.d("디버그", "prevPath $prevPath")
        var file = File(prevPath)

        // 파일이 디렉토리가 아닐경우
        if (!file.isDirectory) {
            toast("디렉토리가 아닙니다.")
            return
        }

        val fileList = file.list()
        items.clear()
        items.add("..")

        fileList.forEach { file ->
            items.add(file)
        }

        listAdapter?.notifyDataSetChanged()

        currentPath = prevPath
    }

    private fun nextPath(path: String) {
        // 현재 경로에서 /와 다음 경로 붙이기
        nextPath = "$currentPath/$path"

        var file = File(nextPath)

        if (!file.isDirectory) {
//            toast("디렉토리가 아닙니다.")
            var intent = Intent(this, ReaderActivitiy::class.java)
            intent.putExtra("path", nextPath)
            startActivity(intent)
            return
        }

        var fileList = file.list()
        items.clear()
        items.add("..")

        fileList.forEach { file ->
            items.add(file)
        }

        listAdapter?.notifyDataSetChanged()

        // 현재 경로는 이미 다음 경로로 지정되어있다.
        currentPath = nextPath
    }

    // 권한 결과 확인
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            permissionsRequestCode -> {
                val isPermissionsGranted =
                    managePermissions.processPermissionsResult(grantResults)

                if (isPermissionsGranted) {
                    toast("권한이 허용되었습니다.")

                    // 초기화
                    init()
                } else {
                    toast("권한 거부되어 정상작동 되지 않습니다.")
                }
                return
            }
        }
    }

}

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}