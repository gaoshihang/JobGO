冒泡排序只会操作相邻的两个数据。每次比较相邻的两个元素，看是否满足大小要求，如果不满足就交换其位置。一次冒泡排序至少会让一个元素到其正确的位置上，循环n次后，该数组就会有序。  

#### 冒泡排序的优化
若某次发现冒泡操作已经没有交换的元素时，说明已经有序，不需要再执行后续的操作。  
如下图所示：  
![冒泡排序优化](https://upload-images.jianshu.io/upload_images/2818100-37a4b13ba88275e0.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)  

代码如下：  
```
public static void bubbleSort(int[] a, int n){
  if(n <= 1) return;
  
  for(int i = 0; i < n;i++){
    boolean stop = false;
    for(int j = 0; j < n - 1 - i;i++){
      int tmp = a[j+1];
      a[j+1] = a[j];
      a[j] = tmp;
      stop = true;
    }
    if(!stop) break;
  }
}


```

#### 冒泡排序的特性
1.冒泡排序的空间复杂度为O(1)，是一个原地排序算法。  
2.冒泡排序是稳定的排序算法，当前后两元素相等时，不进行交换。  
3.最好情况下，要排序的数据已经是有序的了，我们只需要进行一次冒泡操作，就可以结束了，所以最好情况时间复杂度是O(n)。而最坏的情况是，要排序的数据
刚好是倒序排列的，我们需要进行n次冒泡操作，所以最坏情况时间复杂度为O(n2)。
