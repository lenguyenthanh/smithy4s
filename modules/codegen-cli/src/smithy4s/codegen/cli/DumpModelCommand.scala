/*
 *  Copyright 2021 Disney Streaming
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

package smithy4s.codegen.cli

import cats.implicits._
import com.monovore.decline.Command

import Options._

object DumpModelCommand {
  import Smithy4sCommand._

  val options = (
    specsArgs,
    repositoriesOpt.map(_.getOrElse(Nil)),
    dependenciesOpt.map(_.getOrElse(Nil))
  ).mapN(DumpModelArgs.apply)

  val command: Command[DumpModel] =
    Command("dump-model", "Output a JSON view of the Smithy models")(
      options.map(DumpModel.apply)
    )
}