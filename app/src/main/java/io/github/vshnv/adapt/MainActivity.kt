package io.github.vshnv.adapt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import androidx.viewbinding.ViewBinding
import io.github.vshnv.adapt.databinding.ActivityMainBinding
import io.github.vshnv.adapt.databinding.LayoutTextItemBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val adapter = adapt<String> {
        create {
            ViewSource.BindingViewSource(LayoutTextItemBinding.inflate(layoutInflater), ViewBinding::getRoot)
        }.bind { model, layoutTextItemBinding ->
            layoutTextItemBinding.tvTest.text = model
        }
        itemEquals { s, s2 -> s == s2 }
        contentEquals { s, s2 -> s == s2 }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.rvTest.adapter = adapter
        adapter.submitData(listOf("a", "b", "c", "d"))
    }

    override fun onResume() {
        super.onResume()
        adapter.submitData(listOf("a", "b", "c", "f", "e ", "t"))
    }
}