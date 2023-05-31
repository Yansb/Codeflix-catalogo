package com.yansb.catalogo.application;

public abstract class UnitUseCase<IN> {
  public abstract void execute(IN anInput);
}
