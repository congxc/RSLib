# RSLib自己的库 

### 1.公共类RSBaseActivity、RSBaseFragment 何MVP模式的RSBaseMVPActivity

### 2.RxAndroidUtils等工具类

### 3.RecycleView的分组拖拽排序的BaseCategoryAdapter，侧滑关闭Activity的SwipeBackLayout

## How to use

allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
  dependencies {
          annotationProcessor 'com.jakewharton:butterknife-compiler:8.6.0'//不加这个使用butterknife会报错
	        compile 'com.github.congxc:RSLib:v1.0.0'
	}
