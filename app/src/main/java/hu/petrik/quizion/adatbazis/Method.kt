package hu.petrik.quizion.adatbazis

enum class Method (val type:String, val params: String ){
    CREATE("POST","POST"),
    READ("GET","GET"),
    UPDATE("PUT","POST"),
    DELETE("DELETE","GET"),
    INFO("OPTIONS","GET");
}