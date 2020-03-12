#!/bin/bash


cd build 
export LLVM_DIR=~/ug3-ct/build

cmake3 ..
make
cd ..

~/ug3-ct/build/bin/clang -S -emit-llvm -Xclang -disable-O0-optnone dead.c
~/ug3-ct/build/bin/opt -load build/src/libMyPass.so -mem2reg -mypass dead.ll -o dead.bc
~/ug3-ct/build/bin/llvm-dis dead.bc

