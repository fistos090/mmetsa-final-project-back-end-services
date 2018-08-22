/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

angular.module("AttendenceWebApp", ["AttendenceWebApp.Controllers","ngRoute"]).config(["$routeProvider",function($routeProvider){
        
        $routeProvider.when("/",{
            templateUrl:"login.html",
            controller:"siteController"
        }).when("/editStudent",{
            templateUrl:"editStudent.html",
            controller:"siteController"
        }).when("/editLecturer",{
            templateUrl:"editLecturer.html",
            controller:"siteController"
        }).when("/lecturerHome",{
            templateUrl:"lecturerHome.html",
            controller:"siteController"
        }).when("/activateReg",{
            templateUrl:"activateReg.html",
            controller:"siteController"
        }).when("/viewSubjects",{
            templateUrl:"viewSubjects.html",
            controller:"siteController"
        }).when("/registerLecturer",{
            templateUrl:"registerLecturer.html",
            controller:"siteController"
        }).when("/addNewCourse",{
            templateUrl:"addNewCourse.html",
            controller:"siteController"
        }).when("/addNewSubject",{
            templateUrl:"addNewSubject.html",
            controller:"siteController"
        }).when("/adminReports",{
            templateUrl:"adminReports.html",
            controller:"siteController"
        }).when("/index",{
            templateUrl:"index.html",
            controller:"siteController"
        }).when("/login",{
            templateUrl:"adminLogin.html",
            controller:"siteController"
        }).when("/register",{
            templateUrl:"adminRegister.html",
            controller:"siteController"
        }).when("/RegisterStudent",{
            templateUrl:"RegisterStudent.html",
            controller:"siteController"
        }).when("/displayAllStudents",{
            templateUrl:"displayStudents.html",
            controller:"siteController"
        }).when("/displayAllLecturers",{
            templateUrl:"viewLecturers.html",
            controller:"siteController"
        }).when("/lecturerReports",{
            templateUrl:"lecturerReports.html",
            controller:"siteController"
        }).when("/profile",{
            templateUrl:"profile.html",
            controller:"siteController"
        }).otherwise({
            redirectTo:"index.html",
            controller:"siteController"
        });
        
        
}]);

