package controllers

import anorm._
import play.api.Play.current
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.DB
import play.api.mvc._

object Application extends Controller {

  case class Registration(email: String,
                          password: String,
                          fullName: String)

  // Form
  // - mapping -> case classes
  // - single -> single form field
  // - tuple -> multiple form fields
  //  val apply: (String, String, String) => Registration = Registration.apply _
  //  val unapply: (Registration) => Option[(String, String, String)] = Registration.unapply _
  val registration = Form(
    mapping(
      "email" -> email,
      "password" -> nonEmptyText,
      "fullName" -> nonEmptyText
    )(Registration)(Registration.unapply)
  )


  def index = Action {
//    registration.fill()

    Ok(views.html.index(registration))
  }

  def register = Action { implicit request =>

    registration.bindFromRequest.fold({
      erroredRegistrationForm => BadRequest(views.html.index(erroredRegistrationForm))
    }, {
      form =>
        DB.withConnection { implicit c =>
          SQL("INSERT INTO User(email, password,fullname,isAdmin) VALUES ({email}, {password},{fullname},{isAdmin})")
          .on (
            'email -> form.email,
            'password -> form.password,
            'fullname -> form.fullName,
            'isAdmin -> false
          ).executeInsert()
        }
        Ok
    })
  }













}