package kot.mvvm.coroutines

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_hash_map_demo.*
import java.util.*

class HashMapDemo : AppCompatActivity() {
    private var myHash = Hashtable<String, String>()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hash_map_demo)

        addItem.setOnClickListener {
            val key = keyEdit.text.toString()
            val value = ValueEdit.text.toString()
            if (key.isEmpty() || value.isEmpty()) {
                Toast.makeText(this, "Fill The Fields", Toast.LENGTH_SHORT).show()
            } else {
                myHash[key] = value
                updateTextView()
            }
        }

        updateItem.setOnClickListener {
            val key = keyEdit.text.toString()
            val value = ValueEdit.text.toString()
            if (key.isEmpty() || value.isEmpty()) {
                Toast.makeText(this, "Fill The Fields", Toast.LENGTH_SHORT).show()
            } else {
                myHash.replace(key, value)
                updateTextView()
            }
        }

        removeItem.setOnClickListener {
            val key = keyEdit.text.toString()
            if (key.isEmpty()) {
                Toast.makeText(this, "Fill The Fields", Toast.LENGTH_SHORT).show()
            } else {
                myHash.remove(key)
                updateTextView()
                 
            }
        }
    }

    private fun updateTextView() {
        var text = ""
        for (element in myHash) {
            text = text + element.key +" : "+ element.value + "\n"
        }
        allText.text = text
    }
}