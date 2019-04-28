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

import cats.data.Kleisli

object kleisli extends TransReaderInstances

private[reader] trait TransReaderInstances {

  implicit def kleisliGenReader[F[_], R]: TransReader[Kleisli, F, R] =
    new TransReader[Kleisli, F, R] {
      def runReader[A](fa: Kleisli[F, R, A])(env: R): F[A] = fa.run(env)
      def unread[A](fa: F[A]): Kleisli[F, R, A]            = Kleisli(_ => fa)
    }

}
