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

import io.github.gvolpe.reader.BiReader
import scalaz.zio.{ Task, TaskR }

object instances extends BiReaderInstances

private[zio] abstract class BiReaderInstances {

  implicit def taskGenReader[R]: BiReader[TaskR, R] =
    new BiReader[TaskR, R] {
      def runReader[A](fa: TaskR[R, A])(env: R): Task[A] = fa.provide(env)
      def unread[A](fa: Task[A]): TaskR[R, A]            = fa
    }

}
