# Lib-FormControl

![alt text](https://raw.githubusercontent.com/manojbhadane/Lib-FormControl/master/Untitled%20design.png)

Add it in your root build.gradle at the end of repositories:
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```	
Add the dependency
```
dependencies {
	        implementation 'com.github.manojbhadane:Lib-FormControl:5.0'
	}
```


# Sample Usage!

sample - 1
```
<com.manojbhadane.lib_formcontrol.CustomInputLayout
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:cil_component="inputBox"
        app:cil_hint="Enter name"
        app:cil_isMandatory="true"
        app:cil_label="Name" />
```

sample - 2 [Email]
```
<com.manojbhadane.lib_formcontrol.CustomInputLayout
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="10dp"
        app:cil_component="inputBox"
        app:cil_hint="Enter Email"
        app:cil_inputType="email"
        app:cil_isMandatory="true"
        app:cil_label="Email" />
```

sample - 3 [Phone number]
```
<com.manojbhadane.lib_formcontrol.CustomInputLayout
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="10dp"
        app:cil_component="inputBox"
        app:cil_hint="Enter Phone"
        app:cil_inputType="phoneNumber"
        app:cil_label="Phone" />
```


**Free Software, Hell Yeah!**

