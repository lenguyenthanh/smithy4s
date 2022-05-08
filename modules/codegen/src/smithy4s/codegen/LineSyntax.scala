package smithy4s.codegen

import cats.implicits._
object LineSyntax {
  implicit class LineInterpolator(val sc: StringContext) extends AnyVal {
    def line(renderables: ToLines.WithValue[_]*): Line = {
      renderAndCombine(renderables.toList)
    }

    private def renderAndCombine(
        renderables: List[ToLines.WithValue[_]]
    ): Line = {
      def aux[A](binding: ToLines.WithValue[A]): Line = {
        val (imports, lines) = binding.render.tupled
        Line(imports, lines.mkString(""))
      }
      val renderLines: List[Line] = renderables.map(r => aux(r))
      sc.parts.toList
        .map(Line(_))
        .zipAll(renderLines, Line.empty, Line.empty)
        .flatMap { case (a, b) => List(a, b) }
        .combineAll
    }

  }
}

