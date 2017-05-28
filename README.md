# CustomSwipeStack

[![License Apache](https://img.shields.io/badge/license-Apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0) [![](https://jitpack.io/v/yoosanghyeon/customswipestack.svg)](https://jitpack.io/#yoosanghyeon/customswipestack)

<img src="https://github.com/yoosanghyeon/CustomSwipeStack/blob/master/customswipestack_demo.gif" width="270" height="480">

- 기존 SwipeStack 라이브러리를 `UP, DOWN Motion을 추가` 하였고 Listener 형태로 제공
- `ViewHolder패턴`을 Sample로 제공
```java
 new SwipeStack.SwipeStackListener() {
            @Override
            public void onViewSwipedToLeft(int position) {

            }

            @Override
            public void onViewSwipedToRight(int position) {

            }

            @Override
            public void onViewSwipedToTop(int position) {

            }

            @Override
            public void onViewSwipedToBottom(int position) {

            }

            @Override
            public void onStackEmpty() {

            }
        });
```

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
        compile 'com.github.yoosanghyeon:customswipestack:1.0.1'
}

```


### Use it in your layout file ###
1. Use the `yoosanghyeon.customswipestack.SwipeStack` view in your XML layout file
2. Set the parent view's `clipChildren` attribute to `false`

*Example:*

```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:clipChildren="false">


    <yoosanghyeon.customswipestack.SwipeStack
        android:id="@+id/swipestack"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:padding="10dp" />

</FrameLayout>

```


### Create an ViewHolder ###

Create an ViewHolder

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


### Assign the adapter to the SwipeStack ###

Last, but not least, assign the adapter to the SwipeStack.

*Example:*

```java
    SwipeStack swipeStackview = (SwipeStack) findViewById(R.id.swipestack);
    SwipeStackAdapter swipeStackAdapter = new SwipeStackAdapter(this, items);
    swipeStackview.setAdapter(swipeStackAdapter);
    swipeStackview.setListener(new SwipeStack.SwipeStackListener() {
              @Override
              public void onViewSwipedToLeft(int i) {

              }

              @Override
              public void onViewSwipedToRight(int i) {

              }

              @Override
              public void onViewSwipedToTop(int i) {

              }

              @Override
              public void onViewSwipedToBottom(int i) {

              }

              @Override
              public void onStackEmpty() {

              }
          });

```

## Callbacks ##

Currently SwipeStack implements the following callbacks:

- the ` SwipeStackListener ` notifies you when a view was swiped to the left / right / up/ down or when the stack becomes empty.
- the ` SwipeProgressListener ` notifies you about the progress when the user starts / stops dragging a view around.


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
