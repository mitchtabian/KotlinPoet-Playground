//package com.codingwithmitch.processor
//
//class AppModule_ProvidePersonFactory: Factory<Person>{
//
//    override fun get(): Person {
//        return providePerson()
//    }
//
//    companion object {
//        private val INSTANCE = AppModule_ProvidePersonFactory()
//
//        fun providePerson(): Person {
//            return AppModule.INSTANCE.providePerson()
//        }
//
//        fun create(): AppModule_ProvidePersonFactory {
//            return INSTANCE
//        }
//    }
//}
//
//class AppModuleDependencies {
//
//    val person by lazy {
//        AppModule_ProvidePersonFactory.create().get()
//    }
//}
//
//
//
//
//
//
