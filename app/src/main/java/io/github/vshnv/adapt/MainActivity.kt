package io.github.vshnv.adapt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.viewbinding.ViewBinding
import io.github.vshnv.adapt.databinding.ActivityMainBinding
import io.github.vshnv.adapt.databinding.LayoutTextItemBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var adapter: AdaptAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        adapter = adapt {
            create {
                ViewSource.BindingViewSource(LayoutTextItemBinding.inflate(layoutInflater), ViewBinding::getRoot)
            }.bind { model, layoutTextItemBinding ->
                layoutTextItemBinding.tvTest.text = model
            }
        }
        binding.rvTest.adapter = adapter
        GlobalScope.launch {
            val list = mutableListOf<Int>()
            for (i in (1..1000)) {
                delay(1000)
                list.add(i)
                if (i % 9 == 0) {
                    list.shuffle()
                }
                withContext(Dispatchers.Main) {
                    adapter.submitData(list.map { it.toString() })
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }
}