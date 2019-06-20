package com.example.jreader;

import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log


class ManagePermissions(private val activity: Activity, private val list: List<String>, private val code: Int) {
    // 런타임에서 권한 확인
    fun checkPermissions() {
        if (isPermissionsGranted() != PackageManager.PERMISSION_GRANTED) {
            showAlert()
        } else {
            activity.toast("권한들이 이미 허용되었습니다.")

            // 권한이 이미 있더라도 퍼미션을 요청합니다.
            requestPermissions()
        }
    }

    // 권한 상태 확인
    private fun isPermissionsGranted(): Int {
        // PERMISSION_GRANTED : Constant Value: 0
        // PERMISSION_DENIED : Constant Value: -1
        var counter = 0
        for (permission in list) {
            counter += ContextCompat.checkSelfPermission(activity, permission)
        }
        return counter
    }

    // 거부된 권한 찾기
    private fun deniedPermission(): String {
        for (permission in list) {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    permission
                ) == PackageManager.PERMISSION_DENIED
            ) return permission
        }
        return ""
    }

    // 권한 요청 다이얼로그 보여주기
    private fun showAlert() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("권한이 필요합니다.")
        builder.setMessage("일부 일들을 처리하기 위해서는 권한이 필요합니다.")
        builder.setPositiveButton("확인") { _, _ -> requestPermissions() }
        builder.setNeutralButton("취소", null)

        val dialog = builder.create()
        dialog.show()
    }

    // 런타임 시 권한 요청
    private fun requestPermissions() {
        val permission = deniedPermission()
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {

            // 사용자가 한번 권한을 거부 할 경우 실행되는 부분
            // 그냥 권한을 다시 요청한다.
            ActivityCompat.requestPermissions(activity, list.toTypedArray(), code)
            Log.d("디버그", "권한허용")
        } else {
            Log.d("디버그", "권한허용2")
            ActivityCompat.requestPermissions(activity, list.toTypedArray(), code)
        }
    }

    // 권한 결과 처리
    fun processPermissionsResult(grantResults: IntArray): Boolean {
        var result = 0
        if (grantResults.isNotEmpty()) {
            for (item in grantResults) {
                result += item
            }
        }

        if (result == PackageManager.PERMISSION_GRANTED) return true
        return false
    }
}
