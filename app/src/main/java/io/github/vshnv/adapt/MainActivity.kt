package io.github.vshnv.adapt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewbinding.ViewBinding
import io.github.vshnv.adapt.databinding.ActivityMainBinding
import io.github.vshnv.adapt.databinding.LayoutTextItemBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var adapter: AdaptAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        adapter = adapt {
            defineViewTypes { data, position ->
                if (data.toInt() % 5 == 0 || data.toInt() % 15 == 0) {
                    2
                } else 1
            }
            create(1) {
                ViewSource.BindingViewSource(LayoutTextItemBinding.inflate(layoutInflater), ViewBinding::getRoot)
            }.bind {
                binding.tvTest.text = "$data TYPE_1"
            }
            create(2) {
                ViewSource.BindingViewSource(LayoutTextItemBinding.inflate(layoutInflater), ViewBinding::getRoot)
            }.bind {
                binding.tvTest.text = data + " TYPE_2"
            }
        }
        binding.rvTest.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
    }
}