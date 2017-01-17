package com.fz.classification

import com.fz.util.Utils
import org.apache.spark.mllib.tree.DecisionTree

/**
 *决策树-分类封装算法   Labels should take values {0, 1, ..., numClasses-1}
 *  可用于二元分类、多元分类
 * 输入参数：
 * testOrNot : 是否是测试，正常情况设置为false
 * input：输出数据；
 * minPartitions: 输入数据最小partition个数
 * output：输出路径
 * targetIndex：目标列所在下标，从1开始
 * splitter：数据分隔符；
 * impurity：不纯度度函数，分类的有：信息熵(entropy)和基尼指数(gini)
 * maxDepth：树的最大深度，
 * numClasses: 类别的个数
 * maxBins：设定分裂数据集  suggestion 32
 * Created by cuihuan on 2017/1/16.
 */
object DecisionTreeClassification {
  def main (args: Array[String]) {
    if(args.length != 10){
      println("Usage: com.fz.classification.DecisionTreeClassification testOrNot input minPartitions output targetIndex " +
        "splitter impurity maxDepth numClasses maxBins")
      System.exit(-1)
    }
    val testOrNot = args(0).toBoolean // 是否是测试，sparkContext获取方式不一样, true 为test
    val input = args(1)
    val minPartitions = args(2).toInt
    val output = args(3)
    val targetIndex = args(4).toInt // 从1开始，不是从0开始要注意
    val splitter = args(5)
    val impurity = args(6) //
    val maxDepth = args(7).toInt
    val numClasses = args(8).toInt
    val maxBins = args(9).toInt
    // 删除输出，不在Scala算法里面删除，而在Java代码里面删除
    //     Utils.deleteOutput(output)

    val sc =  Utils.getSparkContext(testOrNot,"DecisionTreeClassification create Model")

    // construct data
    // Load and parse the data
    val training = Utils.getLabeledPointData(sc,input,minPartitions,splitter,targetIndex).cache()
    //Empty categoricalFeaturesInfo indicates all features are continuous
    val categoricalFeaturesInfo = Map[Int, Int]()
    // Run training algorithm to build the model
    val model = DecisionTree.trainClassifier(training,numClasses,categoricalFeaturesInfo,impurity,maxDepth,maxBins)
    // save model

    model.save(sc,output)

    sc.stop()
  }

}
