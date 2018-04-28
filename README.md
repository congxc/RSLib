# RSLib自己的库 

### 1.公共类RSBaseActivity、RSBaseFragment 何MVP模式的RSBaseMVPActivity

### 2.RxAndroidUtils等工具类

### 3.RecycleView的分组拖拽排序的BaseCategoryAdapter，侧滑关闭Activity的SwipeBackLayout,SwipeMenuLayout

## How to use

allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
  dependencies {
          implementation com.jakewharton:butterknife:8.6.0//注意
        使用butterknife不需要在Activity（Fragment）的onCreate（）中bind 直接 使用即可 annotationProcessor 'com.jakewharton:butterknife-compiler:8.6.0'//不加这个使用butterknife会报错
	  
	        compile 'com.github.congxc:RSLib:v1.0.0'
	}
