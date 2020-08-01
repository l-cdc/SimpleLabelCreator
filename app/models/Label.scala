package models

case class Label(line1: String, line2: String, line3: String, line4: String) {
  def paragraph = List(line1, line2, line3, line4).mkString("\n")
}
