# GenerateViewBind 
为配合 https://github.com/huangyuanlove/AndroidAnnotation 注解写的代码生成插件，类似`Android ButterKnife Zelezny`插件和 和 `butterknife`依赖库的关系
部分代码来自https://github.com/avast/android-butterknife-zelezny
## 使用

#### 安装
仓库目录下有编译好的插件压缩包，下载此压缩包，在AndroidStudio中找到 "preference-->plugin-->install from disk"

#### 使用
与`Android ButterKnife Zelezny`一致，将该插件功能放在了`Generate`菜单的最后一项
1. 将光标放置于`R.layout.xxx`的`xxx`中,然后调出`Generate`菜单(快捷键：command+n),点击`GenerateViewBind`

2. 此时插件会自动解析该布局文件中所有包含`android:id`的节点，在弹出窗中展示

3. 在弹出窗中选中需要的控件，点击`confirm`,则会自动生成`@BindView(idStr = "xxxx")`,如果需要生成` @BindView(id = R.id.xxx)`的形式，请选中左下角复选框，这里建议不要选中复选框，在gradle6.5之后，application module中生成的R文件中，id不再是final类型

4. 若需要生成 `@ClickResponder`,选中`OnClick`复选框即可
#### 再次开发
需使用IDEA进行开发，社区版、专业版均可

