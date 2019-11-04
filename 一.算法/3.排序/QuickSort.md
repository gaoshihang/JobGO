快排利用的也是分治思想，但是其思路与归并排序完全不同。  
**快排的核心思想**：如果要排序数组是下标从p到r之间的一组数据，我们选择p到r之间任意一个数据作为pivot（分区点）。遍历p到r之间的数据，将小于pivot的
放在左边，大于pivot的放在右边，将pivot放在中间。经过这个步骤后，数组p到r之间的数据被分为了三个部分，前面p到q-1之间的都是小于pivot的，中间q是pivot，
后面q+1到r之间是大于pivot的。  

![快速排序1](https://upload-images.jianshu.io/upload_images/2818100-1dba9073cd617d88.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)  

根据分治、递归的处理思想，可以用递归排序下标从p到q-1之间的数据和下标q+1到r之间的数据，直到区间缩小为1，说明所有的数据都有序了。  

其递推公式如下：
```
递推公式：
quick_sort(p...r) = quick_sort(p...q-1) + quick_sort(q+1...r)
         
终止条件：
p >= r
```

实现代码如下：
```
public void quickSort(int[] a, int p, int r){
  if(p >= r) return;
  
  int q = partition(a, p, r);
  quickSort(a, p, q-1);
  quickSort(a, q+1, r);
}

public int partition(int[] a, int p, int r){
  int pivot = a[r];
  int i = p;
  for(int j = p; j < r; j++){
    if(a[j] < pivot){
      int tmp = a[j];
      a[j] = a[i];
      a[i] = tmp;
      i++;
    }
  }
  
  int tmp = a[i];
  a[i] = pivot;
  a[r] = tmp;
  return i
  
}
```
这里有一个partition分区函数，其主要作用是随机选择一个元素作为pivot，然后对a[p...r]分区，函数返回pivot的下标。  
它的处理过程类似于选择排序。通过游标i把a[p...r-1]分为两部分。a[p...i-1]的元素都是小于pivot的，称为“已处理区间”，a[i...r-1]是“未处理区间”。
每次都从未处理区间a[i...r-1]中取出一个元素a[j]，与pivot比较，如果小于pivot，就将其加入到已处理区间的尾部，也就是a[i]的位置。  

#### 快速排序与归并排序的区别
1.快排不是稳定的排序算法。  
2.归并排序的处理过程是由下到上的，先处理子问题，再合并；快速排序刚好相反，处理过程由上到下，先分区，再处理子问题。  
3.归并排序是非原地排序算法。  













