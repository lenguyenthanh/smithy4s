/*
 *  Copyright 2021-2022 Disney Streaming
 *
 *  Licensed under the Tomorrow Open Source Technology License, Version 1.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     https://disneystreaming.github.io/TOST-1.0.txt
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package demo

import smithy4s.codegen.ModelLoader
import smithy4s.dynamic.DynamicSchemaIndex

object Main extends App {
  try {
    println(smithy.api.NonEmptyString("nope").value)
  } catch {
    case _: java.lang.ExceptionInInitializerError =>
      println("failed")
      sys.exit(1)
  }

  def buildSchemaIndex() =
    ModelLoader
      .load(
        specs = Set.empty,
        dependencies = List(
          "com.disneystreaming.smithy4s::smithy4s-dynamic:0.15.1",
          "com.disneystreaming.smithy4s::smithy4s-codegen:0.15.1"
        ),
        repositories = Nil,
        transformers = Nil,
        discoverModels = true,
        localJars = Nil
      )
      ._2

  val model = buildSchemaIndex()
  DynamicSchemaIndex.loadModel(model).toTry.get
}
