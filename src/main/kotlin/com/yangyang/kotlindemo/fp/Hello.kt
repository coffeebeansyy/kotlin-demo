package com.yangyang.kotlindemo.fp

import java.io.File

sealed class Maybe<out T>{
    object `Nothing`:Maybe<kotlin.Nothing>(){
        override fun toString(): String = "Nothing"
    }
    data class Just<out T>(val value:T):Maybe<T>()
}
fun <T,R>Maybe<T>.fmap(f:(T)->R):Maybe<R> = when(this){
    Maybe.Nothing -> Maybe.Nothing
    is Maybe.Just -> Maybe.Just(f(this.value))
}
infix fun<T,R> ((T)->R).`($)`(maybe: Maybe<T>) = maybe.fmap(this)

fun <T,R> Iterable<T>.fmap(f:(T)->R):List<R> = this.map(f)

fun<T,U,R> ((T)->U).fmap(f:(U)->R) = {t:T -> f(this(t))}

infix fun<T,R> Maybe<(T)->R>.`(*)`(maybe: Maybe<T>):Maybe<R> = when(this){
    Maybe.Nothing -> Maybe.Nothing
    is Maybe.Just -> this.value `($)` maybe
}

infix fun<T,R> Iterable<(T)->R>.`(*)`(iterable: Iterable<T>) = this.flatMap { iterable.map(it) }

fun <T>((x:T,y:T)->T).liftA2(m1:Maybe<T>,m2:Maybe<T>) = {y:T -> {x:T -> this(x,y)}} `($)` m1 `(*)` m2

fun half(x:Int) = if(x%2 == 0) Maybe.Just(x/2) else Maybe.Nothing

//infix fun <T,R> Monad<T>.`))=`(f:(T)->Monad<R>))::Monad<R>
infix fun<T,R>Maybe<T>.`))=`(f:((T)->Maybe<R>)):Maybe<R> = when(this){
    Maybe.Nothing -> Maybe.Nothing
    is Maybe.Just -> f(this.value)
}

data class IO<out T>(val `(-`:T)
infix fun <T,R> IO<T>.`))=`(f:((T)->IO<R>)):IO<R> = f(this.`(-`)

fun getLine():IO<String> = IO(readLine() ?: "")//没有参数并获得用户输入
typealias FilePath = String
fun readFile(filename:FilePath):IO<String> = IO(File(filename).readText())
fun putStrLn(str:String):IO<Unit> = IO(println(str))

fun <T> `do`(ioOper:()->IO<T>) = ioOper()

fun main() {

//    getLine()`))=` ::readFile `))=` ::putStrLn

    val foo = `do`{
        val filename = getLine().`(-`
        val content = readFile(filename).`(-`
        putStrLn(content)
    }

/*
    println(Maybe.Just(3) `))=` ::half)
    println(Maybe.Just(4) `))=` ::half)
    println(Maybe.Just(6) `))=` ::half)
    print(Maybe.Just(20)`))=` ::half`))=` ::half`))=` ::half)


    val foo = {x:Int -> x+2}.fmap { x:Int->x*3 }
    println(foo(1)) //9 -- (1+2)*3

    println(Maybe.Just{x:Int ->x+3} `(*)` Maybe.Just(2))
    val resList = listOf<(Int)->Int>({it*2},{it+3}) `(*)` listOf(1,2,3)
    println(resList)


    val res = {y:Int->{x:Int -> x+y}}`($)` Maybe.Just(3)// Maybe.Just{x:Int -> x+3}
//    Maybe.Just{x:Int -> x+3} `($)` Maybe.Just(4)
    println(res `(*)` Maybe.Just(4))

    println({y:Int->{x:Int -> x+y}}`($)` Maybe.Just(3) `(*)` Maybe.Just(4))

    val res2 = {x:Int,y:Int -> x+y}.liftA2(Maybe.Just(3),Maybe.Just(4))
    println(res2)

     */


}
/**
 * test maybe
 */
fun main1() {

    val res = Maybe.Just(3).fmap{it*10}
    println(res)

    val resNothing = Maybe.Nothing.fmap { x:Int -> x+10 }
    println(resNothing)
}