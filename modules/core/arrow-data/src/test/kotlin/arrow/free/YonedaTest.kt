package arrow.free

import arrow.core.ForId
import arrow.core.Id
import arrow.core.fix
import arrow.core.functor
import arrow.test.UnitSpec
import arrow.test.laws.FunctorLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.functor
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class YonedaTest : UnitSpec() {

    val F = Yoneda.functor<ForId>()

    val EQ = Eq<YonedaOf<ForId, Int>> { a, b ->
        a.fix().lower() == b.fix().lower()
    }

    init {

        "instances can be resolved implicitly" {
            functor<YonedaPartialOf<ForId>>() shouldNotBe null
        }

        testLaws(FunctorLaws.laws(F, { Yoneda(Id(it)) }, EQ))

        "toCoyoneda should convert to an equivalent Coyoneda" {
            forAll { x: Int ->
                val op = Yoneda(Id(x.toString()))
                val toYoneda = op.toCoyoneda().lower(Id.functor()).fix()
                val expected = Coyoneda(Id(x), Int::toString).lower(Id.functor()).fix()

                expected == toYoneda
            }
        }
    }
}
