# CustomSwipeStack

[![License Apache](https://img.shields.io/badge/license-Apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

## QuickStart ##

```gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```
and:

```gradle
dependencies {
        compile 'com.github.yoosanghyeon:customswipestack:1.0.0'
}
```

### Create an ViewHolder ###

Create Adapter in ViewHolder

*Example:*
```java
public class SwipeItemHolder {

    private TextView textView;

    public SwipeItemHolder(View itemView) {
        textView = (TextView) itemView.findViewById(R.id.swipestack_textview);
    }

    public void onBindData(String text){
        textView.setText(text);
    }
}
```



### Create an adapter ###

Create an adapter which holds the data and creates the views for the stack.

*Example:*

```java
public class SwipeStackAdapter extends BaseAdapter {

private Context mContext;
private ArrayList<String> itemDatas;


public SwipeStackAdapter(Context mContext, ArrayList<String> itemDatas) {
    this.mContext = mContext;
    this.itemDatas = itemDatas;
}

@Override
public int getCount() {
    return itemDatas.size();
}

@Override
public Object getItem(int position) {
    return itemDatas.get(position);
}

@Override
public long getItemId(int position) {
    return position;
}

@Override
public View getView(int position, View convertView, ViewGroup viewGroup) {

    SwipeItemHolder swipeItemHolder;
    if (convertView == null) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.cardviewitem, viewGroup, false);
        swipeItemHolder = new SwipeItemHolder(convertView);
        convertView.setTag(swipeItemHolder);
    } else {
        swipeItemHolder = (SwipeItemHolder) convertView.getTag();
    }

    swipeItemHolder.onBindData(itemDatas.get(position));
    return convertView;

}
```
## Copyright Notice ##
```
Copyright (C) 2017 Yoo Sanghyeon

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 ```