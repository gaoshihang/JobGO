spark shuffle：具有某种共同特征的一类数据需要最终汇聚（aggregate）到一个计算结点进行计算，这个数据重新打乱然后汇聚到不同结点的过程就是shuffle。  

老版本：HashBaseShuffle产生的临时文件数：MapTask * ReduceTask。产生过多的临时文件。  
新版本：SortBasedShuffle产生的文件数：MapTask数量。  

但如果Shuffle不落地：  
（1）可能造成内存溢出。  
（2）当某分区丢失时，会重新计算所有父分区数据。  

