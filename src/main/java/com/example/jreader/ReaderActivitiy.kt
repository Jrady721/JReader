package com.example.jreader

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_reader_activitiy.*
import java.io.File
import java.io.FileNotFoundException
import java.nio.charset.Charset

class ReaderActivitiy : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reader_activitiy)
        var path = intent.getStringExtra("path")
        Log.d("디버그", "경로 $path")

        var someFile = File(path)

        // 파일을 문자열로 읽음, 캐릭터셋 기본값은 UTF-8로 불렁모
        // 파일이 존재하지 않을 경우 FileNotFoundException 발생
        // 파일을 한 번에 모두 읽기 때문에 2GB 크기 제한을 가짐
        try {
            var charset = "EUC-KR"
            var text = someFile.readText(Charset.forName(charset))
            Log.d("디버그", "성공")
            textView.text = text

            // TODO 한번에 텍스트를 가져오기에는 너무 무겁다. 자동으로 어느 정도 스크롤 될 때마다 텍스트를 불러오는 기능을 구현해볼 만하다.
        } catch (e: FileNotFoundException) {
            Log.d("디버그", "FileNotFound: $path")
        }

    }
}
