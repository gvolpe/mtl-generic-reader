/*
 * Copyright 2019 Gabriel Volpe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.gvolpe.reader.zio

import cats._
import cats.implicits._
import io.github.gvolpe.reader.{ CheckLaws, GenReader, GenReaderLaws }
import scalaz.zio.{ DefaultRuntime, Runtime, Task, TaskR, ZIO }
import scalaz.zio.interop.catz._

object lawstest extends App with CheckLaws with TaskEqInstances {
  import instances._

  implicit val runtime = new DefaultRuntime {}

  val env: String = "ctx"

  val ga1: Id[Int]   = 123
  val ga3: Task[Int] = 123.pure[Task]

  val fa3: TaskR[String, Int] = ZIO.accessM(_ => ga3)

  check(TaskRGenReaderLaws[String].elimination(fa3, env, ga3))
  check(TaskRGenReaderLaws[String].idempotency(fa3, env))

  println("✔️  All tests have passed! (•̀ᴗ•́)و ̑̑")

}

trait TaskEqInstances {

  implicit def eqTask[A: Eq](implicit rts: Runtime[Any]): Eq[Task[A]] =
    new Eq[Task[A]] {
      def eqv(x: Task[A], y: Task[A]): Boolean =
        Eq[A].eqv(rts.unsafeRun(x), rts.unsafeRun(y))
    }

}

object TaskRGenReaderLaws {
  def apply[R](implicit ev: GenReader[TaskR[R, ?], Task, R]): GenReaderLaws[TaskR[R, ?], Task, R] =
    new GenReaderLaws[TaskR[R, ?], Task, R] { val M = ev }
}
