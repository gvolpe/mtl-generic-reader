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

package io.github.gvolpe.reader.laws

import cats.laws._
import io.github.gvolpe.reader.GenReader

trait GenReaderLaws[F[_], G[_], R] {
  def M: GenReader[F, G, R]

  def elimination[A](fa: F[A], env: R) =
    M.unread(M.runReader(fa)(env)) <-> fa

  def idempotency[A](fa: F[A], env: R) =
    M.runReader(M.unread(M.runReader(fa)(env)))(env) <-> M.runReader(fa)(env)
}
