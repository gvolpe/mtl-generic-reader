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

package io.github.gvolpe.reader

import cats.Eq
import cats.tests.CatsSuite
import io.github.gvolpe.reader.laws.GenReaderLaws
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.{ arbitrary => getArbitrary }
import scalaz.zio._

class ZioGenReaderTest extends CatsSuite {
  import TaskEqInstances._, zio._

  implicit val runtime = new DefaultRuntime {}

  checkAll("TaskR GenReader", GenReaderTests(TaskRGenReaderLaws[String]).genReader[String])

}

object TaskEqInstances {

  implicit def eqTaskR[A: Eq, R: Arbitrary](implicit eqFa: Eq[Task[A]]): Eq[TaskR[R, A]] =
    new Eq[TaskR[R, A]] {
      def eqv(x: TaskR[R, A], y: TaskR[R, A]): Boolean = {
        lazy val env = getArbitrary[R].sample.get
        eqFa.eqv(x.provide(env), y.provide(env))
      }
    }

  implicit def eqTask[A: Eq](implicit rts: Runtime[Any]): Eq[Task[A]] =
    new Eq[Task[A]] {
      def eqv(x: Task[A], y: Task[A]): Boolean =
        Eq[A].eqv(rts.unsafeRun(x), rts.unsafeRun(y))
    }

  implicit def arbTask[R, A: Arbitrary]: Arbitrary[TaskR[R, A]] =
    Arbitrary(Arbitrary.arbitrary[A].map(a => TaskR.access[R](_ => a)))

}

object TaskRGenReaderLaws {
  def apply[R](implicit ev: GenReader[TaskR[R, ?], Task, R]): GenReaderLaws[TaskR[R, ?], Task, R] =
    new GenReaderLaws[TaskR[R, ?], Task, R] { val M = ev }
}
