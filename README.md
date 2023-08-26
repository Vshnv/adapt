# Adapt
Tired of writing Adapters for RecyclerViews? Adapt provides a simple to use DSLs to make writing adapters easier and faster!

## Example Usage

```kotlin
  val adapter = adapt<String> {
            defineViewTypes { data, position ->
                if (data.toInt() % 5 == 0 || data.toInt() % 15 == 0) {
                    2
                } else 1
            }
            create(1) {
                ViewSource.BindingViewSource(LayoutTextItemBinding.inflate(layoutInflater), ViewBinding::getRoot)
            }.bind { model, layoutTextItemBinding ->
                layoutTextItemBinding.tvTest.text = model + " TYPE_1"
            }
            create(2) {
                ViewSource.BindingViewSource(LayoutTextItemBinding.inflate(layoutInflater), ViewBinding::getRoot)
            }.bind { model, layoutTextItemBinding ->
                layoutTextItemBinding.tvTest.text = model + " TYPE_2"
            }
        }

  recyclerView.adapter = adapter


  adapter.submitData(listOf("Test A", "Test B", "Test C"))
```
For further info, checkout the demo app


