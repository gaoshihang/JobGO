选择排序的实现思路类似插入排序，也分为已排序区间和未排序区间。但是选择排序每次会从未排序区间中找出最小的元素，将其放到已排序区间的末尾。  
![选择排序](https://upload-images.jianshu.io/upload_images/2818100-53218e393c9344f6.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)  
代码如下：  
```
public static void selectionSort(int[] a, int n){
  if(n <= 1) return;
  
  for(int i = 0; i < n;i++){
    int min = a[i];
    int minLoc = i;
    for(int j = i; j < n;j++){
      if(a[j] < min){
        min = a[j];
        minLoc = j;
      }
    }
    
    int tmp = a[i];
    a[i] = min;
    a[minLoc] = tmp;
  }
}
```

