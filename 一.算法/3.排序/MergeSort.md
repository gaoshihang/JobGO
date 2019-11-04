归并排序的核心思想：**如果要排序一个数组，先把该数组从中间分成前后两部分，然后对其两部分分别排序，再将排好序的两部分合并在一起，这样整个数组就
都有序了。**  
![归并排序1](https://upload-images.jianshu.io/upload_images/2818100-1dbb0a09f34131a4.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)  

归并排序使用的是分治思想。也就是将一个大问题分解成若干小问题来解决。  
分治思想跟递归思想很相似，分治算法一般都是用递归来实现。  

**写递归代码的技巧：分析得出递推公式，然后找到终止条件，最后将递推公式翻译成递归代码。**  

所以，我们要先写出归并排序的递推公式和终止条件：  
```
递推公式：
merge_sort(p...r) = merge(merge_sort(p...q), merge_sort(q+1...r))
下标q等于p和r的中间位置，也就是(p+r)/2。

终止条件:
p >= r 不用再继续分解
```

以下为代码实现:
```
public void mergeSort(int[] a, int p, int r){
  if(p >= r) return;
  
  int q = (p + r)/2;
  mergeSort(a, p, q);
  mergeSort(a, q+1, r);
  merge(a, p, q, r);
}

public void merge(int[] a, int p, int q, int r){
  int[] tmp = new int[r - p + 1];
  int k = 0;
  int i = p;
  int j = q+1;
  
  while(i <= q && j <= r){
    if(a[i] <= a[j]){
      tmp[k++] = a[i++];
    }else{
      tmp[k++] = a[j++];
    }
  }
  
  while(i <= q){
    tmp[k++] = a[i++];
  }
  
  while(j <= r){
    tmp[k++] = a[j++];
  }
  
  for(int l = 0; l < tmp.length;l++){
    a[p+l] = tmp[l];
  }
}
```

其中，merge这个函数的作用是将已经有序的a[p...q]和a[q+1...r]合并成一个有序数组，并且放入a[p...r]。这个过程是怎么做的呢？   
我们申请一个临时数组tmp，大小与a[p...r]相同，用两个游标i和j，分别指向a[p...q]和a[q+1...r]的第一个元素。比较这两个元素a[i]和a[j]，如果a[i] <= a[j]，
则把a[i]放入到临时数组tmp，且i后移一位，否则将a[j]放入到数组tmp，j后移一位。  
重复以上过程，直到其中一个子数组的所有数据都放入了临时数组中，再把另一个子数组中的数据依次加入到临时数组的末尾，这时，临时数组中存储的就是两个子
数组合并后的有序结果，再将临时数组tmp中的数据拷贝到原数组a[p...r]中。  

#### 归并排序特性
1.归并排序是稳定的排序算法  
2.归并排序的时间复杂度是O(nlogn)  
3.归并排序不是原地排序算法，其空间复杂度为O(n)。  




