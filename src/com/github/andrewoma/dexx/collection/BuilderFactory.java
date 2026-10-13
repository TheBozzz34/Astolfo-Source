package com.github.andrewoma.dexx.collection;

public interface BuilderFactory<E, R> {
   Builder<E, R> newBuilder();
}
