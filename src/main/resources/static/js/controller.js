/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var app = angular.module("AttendenceWebApp.Controllers", ['ngMaterial', 'ngMessages'])
        .controller("siteController", function ($location, $rootScope, $scope, $http, $mdDialog) {

//------------------------------------------------------------------------------------------------------------------------------------------
//                                                  EXECUTE ON CONTROLLER LOAD-UP
//------------------------------------------------------------------------------------------------------------------------------------------
            $rootScope.searchResults = [];

            //READ DATA FROM SESSION STORAGE
            $rootScope.title = sessionStorage.getItem("title");
            $rootScope.loginID = sessionStorage.getItem("sessionID");
            $rootScope.userName = sessionStorage.getItem("userName");
            $rootScope.role = sessionStorage.getItem("role");
            var studentsData = sessionStorage.getItem("students");


            if ($rootScope.timer) {
                $rootScope.isRunning = true;
            } else {
                $rootScope.isRunning = false;
            }
            //Http request that execute once on first time application load-up
            if ($rootScope.loginID !== null) {

                var cData = sessionStorage.getItem("courses");
                var sData = sessionStorage.getItem("subjects");

                //================================================================ Determine Changes ====================================================
                var newSubject = "no";
                var newCourse = "no";

                if (sessionStorage.getItem("newSubjectAdded") !== null) {
                    newSubject = sessionStorage.getItem("newSubjectAdded");
                }
                if (sessionStorage.getItem("newCourseAdded") !== null) {
                    newCourse = sessionStorage.getItem("newCourseAdded");
                }


                //================================================================ LOAD COURSES ====================================================

                if (cData === null || newCourse.toUpperCase() === "yes".toUpperCase()) {

                    $scope.courses = [];
                    if (sessionStorage) {
                        // LocalStorage is supported!
                        $http.get("/TUT/course/displayCourses/" + $rootScope.loginID).then(function (response) {

                            $scope.courses = response.data.courses;
                            var cData = JSON.stringify($scope.courses);
                            //populate session storage with products'data from the backend database
                            sessionStorage.setItem("courses", cData);
                            //parseIntoArray(cData);


                            if (response.data.status.toUpperCase() === "ok".toUpperCase()) {
                                sessionStorage.setItem("newCourseAdded", "no");
                            }

                        });
                    }

                } else {
                    parseCoursesArray(cData);
                }

                //================================================== LOAD SUBJECTS ====================================================

                if (sData === null || newSubject.toUpperCase() === "yes".toUpperCase()) {

                    $scope.subjects = [];

                    if (sessionStorage) {
                        // LocalStorage is supported!
                        $http.get("/TUT/subject/displaySubjects/" + $rootScope.loginID).then(function (response) {

                            $scope.subjects = response.data.subjects;
                            var sData = JSON.stringify($scope.subjects);
                            //populate session storage with products'data from the backend database
                            sessionStorage.setItem("subjects", sData);
                            //parseIntoArray(cData);
                            if (response.data.status.toUpperCase() === "ok".toUpperCase()) {
                                sessionStorage.setItem("newSubjectAdded", "no");
                            }

                        });
                    }

                } else {
                    parseSubjectsArray(sData);
                }

            }



            if (sessionStorage.getItem("whosOn") !== null) {

                if (sessionStorage.getItem("whosOn").toUpperCase() === "ADMIN") {
                    $rootScope.adminOn = true;
                    $rootScope.lecOn = false;
                } else
                if (sessionStorage.getItem("whosOn").toUpperCase() === "LECTURER") {
                    $rootScope.lecOn = true;
                    $rootScope.adminOn = false;
                }
            }

            if (studentsData !== null)
            {
                parseIntoArray(studentsData);
            }

            if ($rootScope.title === null)
            {
                $rootScope.title = "TUT AS [LOGIN]";
                sessionStorage.setItem("title", $rootScope.title);
                document.getElementById("title").innerHTML = $rootScope.title;
            } else {
                document.getElementById("title").innerHTML = $rootScope.title;

            }

            document.getElementById("sysTime").innerHTML = runDate();

            window.setInterval(function () {

                document.getElementById("sysTime").innerHTML = runDate();

            }, 1000);



            function runDate() {
                var days = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];
                var months = ['Januay', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'];
                var currentDate = new Date();

                var min = currentDate.getMinutes();
                var hour = currentDate.getHours();

                if (min < 10) {
                    min = '0' + min;
                }
                if (hour < 10) {
                    hour = '0' + hour;
                }

                var siteTime = days[currentDate.getDay()] + " " + currentDate.getDate() + "-" + months[currentDate.getMonth()] + "-" +
                        currentDate.getFullYear() + " | " + hour + ":" + min;

                return siteTime;
            }

//===================================================================================================   
            //VARIABLE DECLARATION
            $scope.selectedList = [];
            $scope.adm = true;
            $scope.supportOP = false;
            $scope.studentOP = false;
            $scope.lecturerOP = false;

//------------------------------------------------------------------------------------------------------------------------------------------
//                                                  HELPER FUNCTIONS <<GENERAL>>
//------------------------------------------------------------------------------------------------------------------------------------------

            $scope.selectAll = function (AllSubjects) {

                var allState = document.getElementById("ALL").checked;

                for (var i = 0, max = AllSubjects.length; i < max; i++) {

                    document.getElementById(AllSubjects[i].subjectCode).checked = allState;
                    $scope.addAndRemove($scope.selectedList, AllSubjects[i].subjectCode);
                }
            };

            $scope.putByCourseCodeAll = function (selected, systemSubjects, courseCode) {

                for (var i = 0, max = systemSubjects.length; i < max; i++) {

                    var isUnique = selected.indexOf(systemSubjects[i]);

                    if (isUnique <= -1 && courseCode === systemSubjects[i].courseCode) {

                        selected.push(systemSubjects[i]);
                    }
                }

            };

            $scope.sortByCourseCode = function (code) {

                if (code.indexOf("|") > -1) {
                    code = code.slice(0, (code.indexOf("|") - 1));
                }

                $rootScope.subjectsToDisplay = [];

                var sData = sessionStorage.getItem("subjects");

                parseSubjectsArray(sData);

                for (var i = 0, max = $scope.subjects.length; i < max; i++) {

                    if ($scope.subjects[i].courseCode.toUpperCase() === code.toUpperCase()) {

                        $rootScope.subjectsToDisplay.push($scope.subjects[i]);
                    }
                }
            };

            function parseSubjectsArray(sData) {
                $scope.subjects = [];

                var jsonObj = JSON.parse(sData);

                for (var i = 0, max = jsonObj.length; i < max; i++) {

                    var subject = {
                        subjectCode: jsonObj[i].subjectCode,
                        courseCode: jsonObj[i].courseCode,
                        subjectDesc: jsonObj[i].subjectDesc
                    };
                    $scope.subjects.push(subject);
                }

            }

            function parseCoursesArray(cData) {
                $scope.courses = [];

                var jsonObj = JSON.parse(cData);

                for (var i = 0, max = jsonObj.length; i < max; i++) {

                    var course = {
                        id: jsonObj[i].id,
                        courseCode: jsonObj[i].courseCode,
                        courseDescription: jsonObj[i].courseDescription

                    };
                    $scope.courses.push(course);
                }
            }

//     $scope.showSupportOp = function(){
//       
//  
//        
//     };
            $scope.showStudentOP = function () {

                if (!$scope.studentOP) {

                    $scope.studentOP = true;
                } else {

                    $scope.studentOP = false;
                }

            };
            $scope.showLecturerOP = function () {

                if (!$scope.lecturerOP) {

                    $scope.lecturerOP = true;
                } else {

                    $scope.lecturerOP = false;
                }
            };

//Converting server response data into JavaScript array of objects
            function parseIntoArray(studentsData) {
                $rootScope.allStudents = [];

                var jsonObj = JSON.parse(studentsData);

                for (var i = 0, max = jsonObj.length; i < max; i++) {

                    var student = {
                        id: jsonObj[i].id,
                        studentNumber: jsonObj[i].studentNumber,
                        lastname: jsonObj[i].lastname,
                        firstname: jsonObj[i].firstname,
                        cellnumber: jsonObj[i].cellnumber,
                        address: jsonObj[i].address,
                        email: jsonObj[i].email,
                        courseCode: jsonObj[i].courseCode,
                        thumb: jsonObj[i].thumb,
                        idNumber: jsonObj[i].idNumber,
                        gender: jsonObj[i].gender

                    };
                    $rootScope.allStudents.push(student);
                }

            }

            $scope.goto = function (url) {

                if (url === "/editLecturer") {
                    $rootScope.title = "TUT AS [UPDATE LEC]";
                    sessionStorage.setItem("title", $rootScope.title);
                }
                if (url === "/lecturerHome") {
                    $rootScope.title = "TUT AS [LEC HOME]";
                    sessionStorage.setItem("title", $rootScope.title);
                }
                if (url === "/login") {

                    $rootScope.title = "TUT AS [LOGIN]";
                    sessionStorage.setItem("title", $rootScope.title);
                }
                if (url === "/RegisterStudent") {
                    $rootScope.title = "TUT AS [REGISTER STUDENT]";
                    sessionStorage.setItem("title", $rootScope.title);

                    $rootScope.student = {courseCode: '-- Select course code --'};
                }
                if (url === "/displayAllStudents") {
                    $rootScope.title = "TUT AS [DISPLAY STUDENTS]";
                    sessionStorage.setItem("title", $rootScope.title);
                }
                if (url === "/addNewSubject") {
                    $rootScope.title = "TUT AS [NEW SUBJECT]";
                    sessionStorage.setItem("title", $rootScope.title);

                    $rootScope.subject = {courseCode: '-- Select course code --'};
                }
                if (url === "/addNewCourse") {
                    $rootScope.title = "TUT AS [NEW COURSE]";
                    sessionStorage.setItem("title", $rootScope.title);

                }
                if (url === "/editStudent") {
                    $rootScope.title = "TUT AS [UPDATE STUDENT]";
                    sessionStorage.setItem("title", $rootScope.title);
                }
                if (url === "/index") {
                    $rootScope.title = "TUT AS [HOME]";
                    sessionStorage.setItem("title", $rootScope.title);
                }
                if (url === "/registerLecturer") {
                    $rootScope.title = "TUT AS [REGISTER LECTURER]";
                    sessionStorage.setItem("title", $rootScope.title);

                }
                if (url === "/activateReg") {
                    $rootScope.title = "TUT AS [ACTIVATE REGISTER]";
                    sessionStorage.setItem("title", $rootScope.title);

                    $rootScope.register = {registerDate: runDate(),
                        lecturerNo: $rootScope.userNumber,
                        venue: '--Select venue--',
                        subjectCode: '--Select subject--',
                        time: 60};
                }
                if (url === "/viewSubjects") {
                    $rootScope.title = "TUT AS [LECTURER'S SUBJECTS]";
                    sessionStorage.setItem("title", $rootScope.title);
                }

                if (url === "/displayAllLecturers") {
                    $rootScope.title = "TUT AS [DISPLAY LECTURERS]";
                    sessionStorage.setItem("title", $rootScope.title);
                }

                $rootScope.subjectsToDisplay = [];

                $location.path(url);
            };

            $scope.addTo = function (selected, subject) {
                var index = -1;

                for (var i = 0, max = selected.length; i < max; i++) {

                    if (selected[i].subjectCode === subject.subjectCode) {
                        index = i;
                    }
                }

                if (index > -1) {
                    alert("Subject is already in lecturer's subjects");
                } else {
                    selected.push(subject);
                }
            };

            $scope.addAndRemove = function (selected, subject) {

                var sub = {
                    subjectCode: subject
                };
                //var index = selected.indexOf(sub);
                var index = -1;

                for (var i = 0, max = selected.length; i < max; i++) {

                    if (selected[i].subjectCode === subject) {
                        index = i;
                    }

                }

                if (index > -1) {
                    selected.splice(index, 1);
                } else {
                    selected.push(sub);
                }

            };

            $scope.changeState = function (selected, subject) {

                var state = selected.indexOf(subject) > -1;
                return state;
            };

            $scope.login = function (user) {

                var url = "/ADMIN/login";

                if (user.usrRole === "LECTURER") {
                    url = "/TUT/lecturer/login";
                }

                if (validateLoginForm(user)) {
                    showSpinner(true);
                    $http.post(url, $scope.user).then(function (response) {
                        showSpinner(false);

                        if (response.data.status === "OK") {

                            $rootScope.userName = "Hi " + response.data.user.surname + " " + response.data.user.name;
                            $rootScope.role = "[Role : " + response.data.user.usrRole + " ]";
                            $rootScope.userNumber = response.data.user.userNumber;


                            $rootScope.register = {registerDate: runDate(),
                                lecturerNo: response.data.user.userNumber,
                                venue: '--Select venue--',
                                subjectCode: '--Select subject--',
                                time: 60};

                            sessionStorage.setItem("lecturerNo", response.data.user.userNumber);
                            sessionStorage.setItem("sessionID", response.data.sessionID);
                            sessionStorage.setItem("userName", $rootScope.userName);
                            sessionStorage.setItem("role", $rootScope.role);
                            sessionStorage.setItem("userType", response.data.user.usrRole);

                            sessionStorage.setItem("whosOn", response.data.user.usrRole);
                            sessionStorage.setItem("loginInfoID", response.data.loginInfoID);

                            if (response.data.user.usrRole.toUpperCase() === 'LECTURER'.toUpperCase()) {

                                $scope.subjects = response.data.mySubjects;
                                $rootScope.lecturerPro = response.data.user;
                                $rootScope.previousRegisters = response.data.preRegisters;
                                var sData = JSON.stringify($scope.subjects);

                                if (response.data.activeRegister !== null) {

                                    if (response.data.activeRegister !== undefined) {

                                        $rootScope.activeRegister = response.data.activeRegister;

                                        $rootScope.activeRegister.sheetDate = getFormattedDate($rootScope.activeRegister.sheetDate);
                                        $rootScope.subjectAttendingStudents = response.data.subjectAttendingStudents;
                                        $rootScope.onSheetStudents = response.data.studentsOnSheet;

                                        $rootScope.attendingStudents = [];


                                        for (var i = 0; i < $rootScope.subjectAttendingStudents.length; i++) {

                                            var studentNumber = $rootScope.subjectAttendingStudents[i].studentNumber;
                                            var index = $rootScope.attendingStudents.indexOf(studentNumber);

                                            if (index < 0) {
                                                var student = {
                                                    id: $rootScope.subjectAttendingStudents[i].id,
                                                    studentNumber: $rootScope.subjectAttendingStudents[i].studentNumber,
                                                    fullName: $rootScope.subjectAttendingStudents[i].lastname + " " + $rootScope.subjectAttendingStudents[i].firstname,
                                                    status: $rootScope.onSheetStudents[i].status
                                                };

                                                $rootScope.attendingStudents.push(student);
                                            }

                                        }
                                    }
                                }

                                sessionStorage.setItem("subjects", sData);
                                $scope.goto("/lecturerHome");

                            } else {

                                $scope.admin = {};
                                $scope.goto("/index");
                            }
                        }
                        //alert(response.data.message);
                        showAlert(response.data.message);
                    });
                }
            };
            $scope.signOut = function () {

                var sessionID = sessionStorage.getItem("sessionID");
                var userType = sessionStorage.getItem("userType");
                var id = sessionStorage.getItem("loginInfoID");

                var url = "/ADMIN/logout/" + id + "/";

                if (userType.toString().toUpperCase() === 'LECTURER'.toUpperCase()) {

                    url = "/TUT/lecturer/logout/" + id + "/";
                }

                if (sessionID !== null) {
                    showSpinner(true);
                    $http.get(url + sessionID).then(function (response) {
                        showSpinner(false);

                        if (response.data.status === "OK") {

                            sessionStorage.clear();
                            localStorage.clear();
                            $rootScope.allStudents = [];

                            $rootScope.userName = null;
                            $rootScope.loginID = null;
                            $scope.goto("/");
                        }
                    });
                }
            };
            $scope.userType = function (user, role) {

                user.usrRole = role;

                if (role === "ADMIN") {
                    $scope.adm = true;
                    $scope.lec = false;
                } else {
                    $scope.adm = false;
                    $scope.lec = true;
                }

            };
            $scope.reset = function () {
                $scope.user = {};
            };

//------------------------------------------------------------------------------------------------------------------------------------------
//                                                  COURSE FUNCTIONS <<GENERAL>>
//------------------------------------------------------------------------------------------------------------------------------------------



            $scope.addNewCourse = function (course) {

                if (validateCourseData(course)) {
                    if (sessionStorage.getItem("sessionID") !== null) {
                        showSpinner(true);
                        $http.post("/TUT/course/add", course).then(function (response) {
                            showSpinner(false);
                            //alert(response.data.message);
                            if (response.data.status.toLocaleUpperCase() === "REGISTERED".toLocaleUpperCase()) {
                                sessionStorage.setItem("newCourseAdded", "yes");
                                //populate session storage with products'data from the backend database
                                $scope.courses = response.data.courses;
                                sessionStorage.setItem("courses", JSON.stringify(response.data.courses));
                                $scope.course = {};
                                $scope.failureAlert = false;
                                $scope.successAlert = true;
                            } else {

                                $scope.failureAlert = true;
                                $scope.successAlert = false;
                            }

                            $scope.message = response.data.message;

                        });

                        course = {};
                    } else {
                        $scope.goto("/");
                        alert("It looks like you didn't log in, Please log in to perform this operation");
                    }
                }

            };

            $scope.editCourse = function (course) {
                alert(JSON.stringify(course));

            };

            $scope.removeCourse = function (course) {

                var message = "Do you want to continue delete: " + course.courseCode + " | " + course.courseDescription
                        + ". Deleting this course will delete all the subject under this course";
                var continueStatus = confirm(message);
                if (continueStatus) {
                    var ID = course.id;

                    if ($rootScope.loginID !== null) {

                        var url = "/TUT/course/remove/" + ID + "/" + $rootScope.loginID;
                        showSpinner(true);
                        $http.get(url).then(function (response) {
                            showSpinner(false);

                            alert(response.data.message);

                            $scope.courses = response.data.courses;
                            sessionStorage.setItem("courses", JSON.stringify($scope.courses));

                            $scope.subjects = response.data.subjects;
                            sessionStorage.setItem("subjects", JSON.stringify($scope.subjects));


                        });
                    } else {
                        $scope.goto("/");
                        alert("It looks like you didn't log in, Please log in to perform this operation");
                    }
                } else {
                    alert("Operation cancelled");
                }

            };

//------------------------------------------------------------------------------------------------------------------------------------------
//                                                  SUBJECT FUNCTIONS <<GENERAL>>
//------------------------------------------------------------------------------------------------------------------------------------------

            $scope.addNewSubject = function (subject) {

                var storedValue = subject.courseCode;

                if (validateSubjectData(subject)) {
                    if (sessionStorage.getItem("sessionID") !== null) {

                        var code = subject.courseCode;
                        subject.courseCode = code.slice(0, (code.indexOf("|") - 1));
                        showSpinner(true);
                        $http.post("/TUT/subject/add", subject).then(function (response) {
                            showSpinner(false);
                            //alert(response.data.message);
                            if (response.data.status.toUpperCase() === "REGISTERED".toUpperCase()) {
                                sessionStorage.setItem("newSubjectAdded", "yes");
                                //populate session storage with products'data from the backend database
                                $scope.subjects = response.data.subjects;
                                sessionStorage.setItem("subjects", JSON.stringify(response.data.subjects));

                                $rootScope.subject = {courseCode: '-- Select course code --'};

                                $scope.failureAlert = false;
                                $scope.successAlert = true;
                            } else {

                                $rootScope.subject.courseCode = storedValue;

                                $scope.failureAlert = true;
                                $scope.successAlert = false;
                            }

                            $scope.message = response.data.message;

                        });


                    } else {
                        $scope.goto("/");
                        alert("It looks like you didn't log in, Please log in to perform this operation");
                    }
                }

            };


            $scope.editSubject = function (subject) {
                alert(JSON.stringify(subject));

            };

            $scope.removeSubject = function (subject) {

                var message = "Do you want to continue delete: " + subject.subjectCode + " | " + subject.subjectDesc
                        + ". Deleting this course will delete all the subject under this course";
                var continueStatus = confirm(message);
                if (continueStatus) {
                    var CODE = subject.subjectCode;

                    if ($rootScope.loginID !== null) {

                        var url = "/TUT/subject/remove/" + CODE + "/" + $rootScope.loginID;
                        showSpinner(true);
                        $http.get(url).then(function (response) {
                            showSpinner(false);
                            alert(response.data.message);

                            $scope.subjects = response.data.subjects;
                            sessionStorage.setItem("subjects", JSON.stringify($scope.subjects));


                        });
                    } else {
                        $scope.goto("/");
                        alert("It looks like you didn't log in, Please log in to perform this operation");
                    }
                } else {
                    alert("Operation cancelled");
                }

            };
//------------------------------------------------------------------------------------------------------------------------------------------
//                                                  STUDENT FUNCTIONS
//------------------------------------------------------------------------------------------------------------------------------------------

            $scope.registerStudent = function (student) {

                student.studSubject = $scope.selectedList;

                var isValid = validateRegistrationForm(student);

                if (sessionStorage.getItem("sessionID") !== null) {
                    if (isValid) {
                        showSpinner(true);
                        $http.post("/TUT/register", student).then(function (response) {
                            showSpinner(false);
                            if (response.data.status === "REGISTERED") {
                                $rootScope.student = {courseCode: '-- Select course code --'};


                                var max = $scope.selectedList.length;
                                document.getElementById("ALL").checked = false;
                                for (var i = 0; i < max; i++) {

                                    document.getElementById($scope.selectedList[i].subjectCode).checked = false;
                                }

                                $scope.selectedList = [];
                                $rootScope.subjectsToDisplay = [];

                                $scope.failureAlert = false;
                                $scope.successAlert = true;

                            } else {

                                $scope.failureAlert = true;
                                $scope.successAlert = false;
                            }

                            $scope.message = response.data.message;

                        });
                    }
                } else {
                    $scope.goto("/");
                    alert("It looks like you didn't log in, Please log in to perform this operation");
                }

            };

            $scope.edit = function (student) {


                $rootScope.editStudent = student;

                for (var i = 0, max = $scope.courses.length; i < max; i++) {
                    if (student.courseCode === $scope.courses[i].courseCode) {

                        $rootScope.editStudent.courseCode = $rootScope.editStudent.courseCode + ' | '
                                + $scope.courses[i].courseDescription;
                    }
                }

                $http.get("/TUT/subject/findStudentSubjects/" + student.studentNumber + "/" + $rootScope.loginID).then(function (response) {

                    if (response.data.status === "OK") {

                        $rootScope.studentSubjects = response.data.subjects;


                        $rootScope.subjectsToDisplay = [];


                        for (var x = 0; x < $rootScope.studentSubjects.length; x++) {

                            var code = $rootScope.studentSubjects[x].subjectCode;
                            for (var i = 0, max = $scope.subjects.length; i < max; i++) {

                                if ($scope.subjects[i].subjectCode.toUpperCase() === code.toUpperCase() &&
                                        $rootScope.subjectsToDisplay.indexOf($scope.subjects[i]) <= -1) {

                                    $rootScope.subjectsToDisplay.push($scope.subjects[i]);
                                }

                            }
                        }
                    }
                });

                $scope.goto("/editStudent");
            };
            $scope.insertEmail = function (student) {
                if (student.studentNumber) {
                    student.email = student.studentNumber + "@tut4life.ac.za";
                }
            };

            $scope.updateStudent = function (student) {

                student.studSubject = $scope.selectedList;

                var valid = validateRegistrationForm(student);


                if ($rootScope.loginID !== null && valid) {

                    var code = student.courseCode;
                    student.courseCode = code.substr(0, (code.indexOf("|") - 1));
                  //  showSpinner(true);
                    $http.post("/TUT/update/" + $rootScope.loginID, student).then(function (response) {
                      //  showSpinner(false);
                        $rootScope.allStudents = response.data.students;
                        $scope.student = {};
                        alert(response.data.message);
                        $scope.displayAllStudents();
                        $scope.goto("/displayAllStudents");
                    });
                }

            };
            $scope.removeStudent = function (student) {

                var message = "Continue to delete - Name: " + student.lastname + " " + student.firstname + " Student Number: " + student.studentNumber;
                var continueStatus = confirm(message);
                if (continueStatus) {
                    var ID = student.id;
                    //alert(ID);
                    if ($rootScope.loginID !== null) {

                        var url = "/TUT/remove/" + ID + "/" + $rootScope.loginID;
                        showSpinner(true);
                        $http.get(url).then(function (response) {
                            showSpinner(false)
                            $rootScope.allStudents = response.data.students;
                            alert(response.data.message);
                            sessionStorage.setItem("students", JSON.stringify($rootScope.allStudents));
                        });
                    } else {
                        $scope.goto("/");
                        alert("It looks like you didn't log in, Please log in to perform this operation");
                    }
                } else {
                    alert("Operation cancelled");
                }
            };

            $scope.displayAllStudents = function () {

                $rootScope.allStudents = [];

                $rootScope.loginID = sessionStorage.getItem("sessionID");
                if ($rootScope.loginID !== null) {
                    showSpinner(true);
                    $http.get("/TUT/displayAllStudents/" + $rootScope.loginID).then(function (response) {
                        showSpinner(false);
                        if (response.data.status === "OK") {
                            $rootScope.allStudents = response.data.students;
                            sessionStorage.setItem("students", JSON.stringify($rootScope.allStudents));
                            $scope.goto("/displayAllStudents");
                        }
                        //alert(response.data.message);
                    });
                } else {
                    $scope.goto("/");
                    alert("It looks like you didn't log in, Please log in to perform this operation");
                }

            };

//------------------------------------------------------------------------------------------------------------------------------------------
//                                                  LECTURER'S FUNCTIONS
//------------------------------------------------------------------------------------------------------------------------------------------
            $scope.absent = {
                "background": "url(../images/absent.png",
                " background-repeat": "no-repeat",
                "height": "30px",
                "float": "right",
                "width": "20%",
                "padding": "2%"
            };

            // $scope.test = function(){alert("dsghsdsdhgjhdg");};
            $scope.change = function (newInfo, lecturerPro) {

                newInfo.lecturerNo = lecturerPro.userNumber;

                $rootScope.loginID = sessionStorage.getItem("sessionID");
                if ($rootScope.loginID !== null) {
                    showSpinner(true);
                    $http.post("/TUT/lecturer/changePassword/" + $rootScope.loginID, newInfo).then(function (response) {
                        showSpinner(false);
                        if (response.data.status === "OK") {
                            $scope.newInfo = {};
                            $scope.lecturerPro = response.data.lecturer;
                        }
                        alert(response.data.message);
                    });

                    $scope.goto('/profile');
                } else {
                    $scope.goto("/");
                    alert("It looks like you didn't log in, Please log in to perform this operation");
                }

            };


            $scope.signRegisterManually = function (stud) {
                //  alert(stud.id);

                var url = "TUT/signRegister/" + stud.id;

                if (stud.status.toUpperCase() === 'PRESENT'.toUpperCase()) {
                    url = "TUT/signRegister/out/" + stud.id;
                }
                $rootScope.loginID = sessionStorage.getItem("sessionID");
                if ($rootScope.loginID !== null) {
                    showSpinner(true);
                    $http.get(url).then(function (response) {
                        showSpinner(false);
                        if (response.data.status === "SIGNED") {

                            $rootScope.onSheetStudents = response.data.studentsOnSheet;

                            max = $rootScope.attendingStudents.length;

                            for (var i = 0; i < max; i++) {
                                $rootScope.attendingStudents[i].status = $rootScope.onSheetStudents[i].status;
                            }

                        }
                    });
                } else {
                    $scope.goto("/");
                    alert("It looks like you didn't log in, Please log in to perform this operation");
                }

            };

            $scope.getSubjectStudents = function (subjectCode) {


                $rootScope.loginID = sessionStorage.getItem("sessionID");
                if ($rootScope.loginID !== null) {
                    showSpinner(true);
                    $http.get("/TUT/subject/getSubjectStudents/" + $rootScope.loginID + "/" + subjectCode).then(function (response) {
                        // alert(response.data.message);
                        showSpinner(false);
                        if (response.data.status === 'OK') {
                            //$scope.report = {};
                            $scope.studentsNumbers = response.data.studentsNumbers;
                        }
                    });
                } else {
                    $scope.goto("/");
                    alert("It looks like you didn't log in, Please log in to perform this operation");
                }
            };

            $scope.generateRangeReport = function (reportData) {
                $rootScope.loginID = sessionStorage.getItem("sessionID");
                if (validateReportsForm(reportData, 'subjectRange')) {
                    if ($rootScope.loginID !== null) {

                        var startDate = {day: reportData.startDate.getDate(),
                            month: reportData.startDate.getMonth(),
                            year: reportData.startDate.getFullYear()};

                        var endDate = {day: reportData.endDate.getDate(),
                            month: reportData.endDate.getMonth(),
                            year: reportData.endDate.getFullYear()};
                        var data = {
                            firstDate: startDate,
                            secondDate: endDate,
                            name: reportData.name,
                            lecturerNo: $rootScope.userNumber,
                            subjectCode: reportData.subjectCode,
                            reportName: reportData.name,
                            todaysDate: runDate()
                        };


                        $http.post("/TUT/lecturer/generateHLReport/" + $rootScope.loginID, data).then(function (response) {
                            //alert(response.data.message);
                            if (response.data.status === 'OK') {

                                $scope.report = {};
                                showDialog(response.data.reportData, 'subject-range-modal-view.html');

                            }
                        });

                    } else {
                        $scope.goto("/");
                        alert("It looks like you didn't log in, Please log in to perform this operation");
                    }
                }

            };

            $scope.retrievePassword = function () {

                showDialog({}, 'forgotPassword.html');
            };


            $scope.generateStudentRangeReport = function (reportData) {
                $rootScope.loginID = sessionStorage.getItem("sessionID");

                if (validateReportsForm(reportData, 'studentRange')) {
                    if ($rootScope.loginID !== null) {

                        var startDate = {day: reportData.startDate.getDate(),
                            month: reportData.startDate.getMonth(),
                            year: reportData.startDate.getFullYear()};

                        var endDate = {day: reportData.endDate.getDate(),
                            month: reportData.endDate.getMonth(),
                            year: reportData.endDate.getFullYear()};

                        var data = {
                            firstDate: startDate,
                            secondDate: endDate,
                            studentNo: reportData.studentNumber,
                            lecturerNo: sessionStorage.getItem("lecturerNo"),
                            subjectCode: reportData.subjectCode,
                            reportName: reportData.name,
                            todaysDate: runDate()
                        };

                        $http.post("/TUT/lecturer/generateStudentRangeReport/" + $rootScope.loginID, data).then(function (response) {
                            //alert(response.data.message);
                            if (response.data.status === 'OK') {

                                //response.data.reportData.records = response.data.records;
                                //alert(JSON.stringify(response.data.reportData));
                                //response.data.reportData.lecturerNo = sessionStorage.getItem("lecturerNo");

                                $scope.studReport = {};
                                showDialog(response.data.reportData, 'modal-view.html');

                            }
                        });

                    } else {
                        $scope.goto("/");
                        alert("It looks like you didn't log in, Please log in to perform this operation");
                    }
                }
            };

            $scope.subjectSpecificDate = function (reportData) {

                $rootScope.loginID = sessionStorage.getItem("sessionID");
                if (validateReportsForm(reportData, 'subjectSecificDate')) {
                    if ($rootScope.loginID !== null) {

                        var reportDate = {day: reportData.reportDate.getDate(),
                            month: reportData.reportDate.getMonth(),
                            year: reportData.reportDate.getFullYear()};

                        var data = {
                            reportDate: reportDate,
                            lecturerNo: $rootScope.userNumber,
                            name: reportData.name,
                            subjectCode: reportData.subjectCode,
                            reportName: reportData.name,
                            todaysDate: runDate()
                        };

                        $http.post("/TUT/lecturer/subjectSpecificDate/" + $rootScope.loginID, data).then(function (response) {

                            // alert(response.data.message);
                            if (response.data.status === 'OK') {

//                                response.data.reportData.todaysDate = runDate();
//                                response.data.reportData.reportName = reportData.name;

                                $scope.subSpecificReport = {};
                                showDialog(response.data.reportData, 'subject-specific-modal-view.html');

                            }
                        });

                    } else {
                        $scope.goto("/");
                        alert("It looks like you didn't log in, Please log in to perform this operation");
                    }
                }

            };

            $scope.generateReport = function (register) {

                $rootScope.loginID = sessionStorage.getItem("sessionID");
                if ($rootScope.loginID !== null) {

                    $http.get("/TUT/lecturer/generateReport/" + $rootScope.loginID + "/" + register.id).then(function (response) {

                        alert(response.data.message);
                    });

                } else {
                    $scope.goto("/");
                    alert("It looks like you didn't log in, Please log in to perform this operation");
                }

            };

            $scope.closeRegister = function (register) {

                $rootScope.loginID = sessionStorage.getItem("sessionID");
                if ($rootScope.loginID !== null) {

                    var data = {sessionID: $rootScope.loginID, subjectCode: register.subjectCode, lecturerNo: sessionStorage.getItem("lecturerNo")};
                    showSpinner(true);
                    $http.post("/TUT/lecturer/closeRegister", data).then(function (response) {
                        showSpinner(false);

                        if (response.data.activeRegister === null) {

                            $rootScope.onSheetStudents = undefined;
                            $rootScope.activeRegister = undefined;
                        }
                        if (response.data.status === "OK") {

                            $rootScope.previousRegisters = response.data.preRegisters;

                            for (var i = 0; i < $rootScope.previousRegisters.length; i++) {

                                $rootScope.previousRegisters[i].sheetDate = getFormattedDate($rootScope.previousRegisters[i].sheetDate);
                            }

                            $scope.stopSync();
                            $scope.goto('/lecturerHome');
                        }

                        alert(response.data.message);
                    });

                } else {
                    $scope.goto("/");
                    alert("It looks like you didn't log in, Please log in to perform this operation");
                }

            };
            $scope.manuallyCheck = function (register) {

                $rootScope.loginID = sessionStorage.getItem("sessionID");
                if ($rootScope.loginID !== null) {
                    showSpinner(true);
                    $http.get("/TUT/lecturer/" + $rootScope.loginID + "/" + register.id).then(function (response) {
                        showSpinner(false);
                        $rootScope.onSheetStudents = response.data.studentsOnSheet;

                        for (var i = 0; i < $rootScope.onSheetStudents.length; i++) {
                            $rootScope.attendingStudents[i].status = $rootScope.onSheetStudents[i].status;

                        }
                    });

                } else {
                    $scope.goto("/");
                    alert("It looks like you didn't log in, Please log in to perform this operation");
                }
            };

            $scope.syncRegister = function (register) {

                $rootScope.timer = window.setInterval(function () {

                    $rootScope.loginID = sessionStorage.getItem("sessionID");

                    $http.get("/TUT/lecturer/" + $rootScope.loginID + "/" + register.id).then(function (response) {

                        $rootScope.onSheetStudents = response.data.studentsOnSheet;

                        for (var i = 0; i < $rootScope.onSheetStudents.length; i++) {

                            $rootScope.attendingStudents[i].status = $rootScope.onSheetStudents[i].status;

                        }

                    });
                }, 1000);
                $rootScope.isRunning = true;
            };

            $scope.stopSync = function () {

                $rootScope.isRunning = false;
                window.clearInterval($rootScope.timer);
                $rootScope.timer = undefined;
            };

            $scope.activateRegister = function (register) {


                var isValid = validateRegisterData(register);
                //alert(JSON.stringify(register));

                var data = {sessionID: sessionStorage.getItem("sessionID"),
                    registerData: register};

                if (sessionStorage.getItem("sessionID") !== null) {
                    if (isValid) {
                        showSpinner(true);
                        $http.post("/TUT/lecturer/activateRegister", data).then(function (response) {
                            showSpinner(false);
                            alert(response.data.message);


                            if (response.data.status === "OK") {

                                $rootScope.register = {registerDate: runDate(),
                                    lecturerNo: response.data.activeRegister.lecturerNo,
                                    venue: '--Select venue--',
                                    subjectCode: '--Select subject--',
                                    time: 60};


                                $rootScope.activeRegister = response.data.activeRegister;
                                $rootScope.activeRegister.sheetDate = getFormattedDate($rootScope.activeRegister.sheetDate);

                                $rootScope.subjectAttendingStudents = response.data.subjectAttendingStudents;
                                $rootScope.onSheetStudents = response.data.studentsOnSheet;

                                var sData = JSON.stringify($scope.subjects);

                                $rootScope.attendingStudents = [];

                                for (var i = 0; i < $rootScope.subjectAttendingStudents.length; i++) {

                                    var studentNumber = $rootScope.subjectAttendingStudents[i].studentNumber;
                                    var index = $rootScope.attendingStudents.indexOf(studentNumber);

                                    if (index < 0) {
                                        var student = {
                                            id: $rootScope.subjectAttendingStudents[i].id,
                                            studentNumber: $rootScope.subjectAttendingStudents[i].studentNumber,
                                            fullName: $rootScope.subjectAttendingStudents[i].lastname + " " + $rootScope.subjectAttendingStudents[i].firstname,
                                            status: $rootScope.onSheetStudents[i].status};

                                        $rootScope.attendingStudents.push(student);
                                    }

                                }


                                sessionStorage.setItem('onRegisterStudents', JSON.stringify($rootScope.attendingStudents));
                                $scope.message = response.data.message;
                                $scope.goto('/lecturerHome');
                            }


                        });
                    }
                } else {
                    $scope.goto("/");
                    alert("It looks like you didn't log in, Please log in to perform this operation");
                }

            };

            $scope.registerLecturer = function (lecturer) {

                lecturer.lecturerSubjects = $scope.selectedList;

                var isValid = validateLecturerData(lecturer,'register');

                if (sessionStorage.getItem("sessionID") !== null) {
                    if (isValid) {
                        showSpinner(true);
                        $http.post("/TUT/lecturer/register", lecturer).then(function (response) {
                            // alert(response.data.message);
                            showSpinner(false);
                            if (response.data.status === "REGISTERED") {
                                $scope.lecturer = {};

                                var max = $scope.selectedList.length;
//                                document.getElementById("ALL").checked = false;
//                                for (var i = 0; i < max; i++) {
//
//                                    document.getElementById($scope.selectedList[i].subjectCode).checked = false;
//
//                                }

                                $scope.selectedList = [];

                                $scope.failureAlert = false;
                                $scope.successAlert = true;
                            } else {

                                $scope.failureAlert = true;
                                $scope.successAlert = false;
                            }

                            $scope.message = response.data.message;

                        });
                    }
                } else {
                    $scope.goto("/");
                    alert("It looks like you didn't log in, Please log in to perform this operation");
                }

            };

            $scope.displayAllLecturers = function () {

                $rootScope.allLecturers = [];
                $rootScope.loginID = sessionStorage.getItem("sessionID");
                if ($rootScope.loginID !== null) {
                    showSpinner(true);
                    $http.get("/TUT/lecturer/displaylectures/" + $rootScope.loginID).then(function (response) {
                        showSpinner(false);
                        if (response.data.status === "OK") {
                            $rootScope.allLecturers = response.data.lecturers;
                            //alert(response.data.message);
                            sessionStorage.setItem("lecturers", JSON.stringify($rootScope.allLecturers));
                            $scope.goto("/displayAllLecturers");
                        }
                    });

                } else {
                    $scope.goto("/");
                    alert("It looks like you didn't log in, Please log in to perform this operation");
                }

            };

            $scope.editLec = function (lecturer) {

                $rootScope.editLecturer = lecturer;

                $http.get("/TUT/subject/findLecturerSubjects/" + lecturer.userNumber + "/" + $rootScope.loginID).then(function (response) {

                    if (response.data.status === "OK") {

                        $rootScope.subjectsToDisplay = response.data.subjects;

                    }
                });

                $scope.goto("/editLecturer");
            };

            $scope.updateLecturer = function (lecturer) {

                lecturer.lecturerSubjects = $rootScope.subjectsToDisplay;
                var valid = validateLecturerData(lecturer, 'update');

                if ($rootScope.loginID !== null && valid) {
                   // showSpinner(true);
                    $http.post("/TUT/lecturer/update/" + $rootScope.loginID, lecturer).then(function (response) {
                       // showSpinner(false);
                        alert(response.data.message);
                        $scope.displayAllLecturers();
                    });
                }

            };
            $scope.removeLecturer = function (lecturer) {

                var message = "Continue to delete - Name: " + lecturer.surname + " " + lecturer.name + " Stuff Number: " + lecturer.userNumber;
                var continueStatus = confirm(message);
                if (continueStatus) {
                    var ID = lecturer.userNumber;
                    //alert(ID);
                    if ($rootScope.loginID !== null) {

                        var url = "/TUT/lecturer/remove/" + ID + "/" + $rootScope.loginID;
                        showSpinner(true);
                        $http.get(url).then(function (response) {
                            showSpinner(false);
                            $rootScope.allLecturers = response.data.lecturers;
                            alert(response.data.message);
                        });
                    } else {
                        $scope.goto("/");
                        alert("It looks like you didn't log in, Please log in to perform this operation");
                    }
                } else {
                    alert("Operation cancelled");
                }
            };

            $scope.insertLecturerEmail = function (lecturer) {

                if (lecturer.name !== undefined && lecturer.surname) {
                    lecturer.email = lecturer.userNumber + "@tut4life.ac.za";
                }
            };
//------------------------------------------------------------------------------------------------------------------------------------------
//                                                  Validation
//------------------------------------------------------------------------------------------------------------------------------------------

            $scope.studNumberStyle = {"border": "red #c0c0c0 solid 1px"};
            $scope.passStyle = {"border": "red #c0c0c0 solid 1px"};
            $scope.cellStyle = {"border": "red #c0c0c0 solid 1px"};

            function validateCourseData(course) {

                var code = /^[0-9A-Za-z]+$/;
                var letters = /^[0-9A-Za-z\s -]+$/;

                if (!course.courseCode.match(code)) {
                    alert('Course code cannot contain special characters.');
                    return false;
                }

                if (!isNaN(course.courseCode)) {
                    alert('Course code cannot be numbers/digits only.');
                    return false;
                }

                if (course.courseCode.length > 7) {
                    alert('Course code must be less than or equal to 7 characters long');
                    return false;
                }

                if (!course.courseDescription.match(letters)) {
                    alert('Course description cannot contain special characters.');
                    return false;
                }

                if (!isNaN(course.courseDescription)) {
                    alert('Course description cannot be numbers/digits only.');
                    return false;
                }

                if (course.courseDescription.length > 100) {
                    alert('Course description must be less than or equal to 100 characters long');
                    return false;
                }

                return true;
            }

            function validateSubjectData(subject) {

                var code = /^[0-9A-Za-z]+$/;
                var letters = /^[0-9A-Za-z\s -]+$/;

                if (!subject.subjectCode.match(code)) {
                    alert('Subject code cannot contain special characters.');
                    return false;
                }

                if (!isNaN(subject.subjectCode)) {
                    alert('Subject code cannot be numbers/digits only.');
                    return false;
                }

                if (subject.subjectCode.length > 7) {
                    alert('Subject code must be less than or equal to 7 characters long');
                    return false;
                }

                if (!subject.subjectDesc.match(letters)) {
                    alert('Subject description contain special characters.');
                    return false;
                }

                if (!isNaN(subject.subjectDesc)) {
                    alert('Subject description cannot be numbers/digits only.');
                    return false;
                }

                if (subject.subjectDesc.length > 100) {
                    alert('Subject description must be less than or equal to 100 characters long');
                    return false;
                }

                if (subject.courseCode === '-- Select course code --') {
                    alert('Course code is not valid.');
                    return false;
                }


                return true;
            }


            function validateReportsForm(reportData, type) {

                if (type === 'studentRange' || type === 'subjectRange') {
                    if (!reportData.startDate) {
                        $scope.formValidationMessage = 'Start date is required';
                        return false;
                    }
                }


                if ((type === 'studentRange' || type === 'subjectRange')) {
                    if (!reportData.endDate) {
                        $scope.formValidationMessage = 'End date is required';
                        return false;
                    }
                }
                if (type === 'studentRange' || type === 'subjectRange') {
                    if (reportData.startDate.getTime() > reportData.endDate.getTime()) {
                        $scope.formValidationMessage = 'Start date cannot be greater than End date';
                        return false;
                    }
                }
                if (type === 'studentRange' || type === 'subjectRange') {
                    if (!angular.isDate(reportData.startDate) || !angular.isDate(reportData.endDate)) {
                        $scope.formValidationMessage = 'One of the dates is invalid.please correct it';
                    }
                }

                if (type === 'subjectSecificDate') {
                    if (!reportData.reportDate) {
                        $scope.formValidationMessage = 'report date is required';
                        return false;
                    }
                }
                if (type === 'studentRange') {
                    if (!reportData.studentNumber) {
                        $scope.formValidationMessage = 'Student number is required';
                        return false;
                    }
                }
                if (!reportData.subjectCode) {
                    $scope.formValidationMessage = 'Subject code is required';
                    return false;
                }
                return true;
            }
            $scope.removeMessage = function () {
                $scope.formValidationMessage = undefined;
            };

            function validateLecturerData(lecturer, type) {

                var userNum = lecturer.userNumber.toString();
                if (userNum.indexOf("-") > -1) {
                    alert("Staff number is invalid : Cannot contain this '-' character");
                    $scope.studNumberStyle = {"border": "red solid 1px"};
                    return false;
                }
                if (userNum.indexOf(".") > -1) {
                    alert("Staff number is invalid : Cannot contain this '.' character");
                    $scope.studNumberStyle = {"border": "red solid 1px"};
                    return false;
                }
                if (userNum.length !== 9) {
                    alert("Staff number must 9 digits");
                    $scope.studNumberStyle = {"border": "red solid 1px"};
                    return false;
                }

                //  ====================================================== surname, name  ======================================================

                var letters = /^[A-Za-z\s -]+$/;

                var emailPattern = /^[0-9A-Za-z . @]+$/;

                if (lecturer.surname.length > 25) {
                    alert("Last name must be less than or equal to 25 characters long");
                    $scope.surnameStyle = {"border": "red solid 1px"};
                    return false;
                }

                if (!lecturer.surname.match(letters)) {
                    alert("Last name is invalid. Must contain letters only");
                    $scope.surnameStyle = {"border": "red solid 1px"};
                    return false;
                }

                if (lecturer.name.length > 25) {
                    alert("First name must be less than or equal to 25 characters long");
                    $scope.nameStyle = {"border": "red solid 1px"};
                    return false;
                }

                if (!lecturer.name.match(letters)) {
                    alert("First name is invalid. Must contain letters only");
                    $scope.nameStyle = {"border": "red solid 1px"};
                    return false;
                }


                if (lecturer.rePassword) {
                    if (lecturer.password !== lecturer.rePassword) {
                        alert("Your passwords do not match");
                        $scope.passStyle = {"border": "red solid 1px"};
                        return false;
                    }
                }
                if (lecturer.password.length < 5) {
                    alert("Password must at least be five characters long");
                    $scope.passStyle = {"border": "red solid 1px"};
                    return false;
                }

                if (type !== 'update') {
                    if (lecturer.password.length > 15) {
                        alert("Password must be less than or equal to 15 characters long");
                        $scope.passStyle = {"border": "red solid 1px"};
                        return false;
                    }
                }

                if (lecturer.lecturerSubjects.length < 1) {
                    alert("Please select subjects before submitting the form");
                    return false;
                }

                return true;
            }
            function validateLoginForm(user) {

                var userNum = user.userNumber.toString();
                if (userNum.indexOf("-") > -1) {
                    alert("Staff number is invalid : Cannot contain this '-' character");
                    $scope.studNumberStyle = {"border": "red solid 1px"};
                    return false;
                }
                if (userNum.indexOf(".") > -1) {
                    alert("Staff number is invalid : Cannot contain this '.' character");
                    $scope.studNumberStyle = {"border": "red solid 1px"};
                    return false;
                }

                if (userNum.length !== 9) {
                    alert("Staff number must 9 digits");
                    $scope.studNumberStyle = {"border": "red solid 1px"};
                    return false;
                }
                if (user.password) {
                    if (user.password.length < 5) {
                        alert("Password must at least be five charecters long");
                        $scope.passStyle = {"border": "red solid 1px"};
                        return false;
                    }
                    if (user.password.length > 15) {
                        alert("Password must be less than or equal to 15 characters long");
                        $scope.passStyle = {"border": "red solid 1px"};
                        return false;
                    }
                } else {
                    alert("Password is required");
                    $scope.passStyle = {"border": "red solid 1px"};
                }
                return true;
            }
            ;


            function validateRegisterData(register) {

                if (register.venue.indexOf('--') > -1) {
                    alert('Invalid venue');
                    $scope.regVenueSty = {"border": "red solid 1px"};
                    return false;
                }
                if (register.subjectCode.indexOf('--') > -1) {
                    alert('Invalid subject code');
                    $scope.subjectCodeSty = {"border": "red solid 1px"};
                    return false;
                }
                return true;
            }
            ;

            $scope.changeVenueStyle = function () {
                $scope.regVenueSty = {"border": "red #c0c0c0 solid 1px"};
            };
            $scope.subjectCodeStyle = function () {
                $scope.subjectCodeSty = {"border": "red #c0c0c0 solid 1px"};
            };

            function validateRegistrationForm(student, action) {


                var message = "";
                var numberPattern = /^[0-9]+$/;
                //
//=========================== student number validation=========================================== 
                //        
                if (isNaN(student.studentNumber)) {
                    alert("Student number is not valid : It must be a numbers/digits");
                    return false;
                }
                var studentNum = student.studentNumber.toString();
                if (!studentNum.match(numberPattern)) {
                    alert("Student number is not valid : Cannot contain special characters");
                    $scope.studNumberStyle = {"border": "red solid 1px"};
                    return false;
                }

                if (studentNum.length !== 9) {
                    alert("Student number must 9 digits");
                    $scope.studNumberStyle = {"border": "red solid 1px"};
                    return false;
                }

                //=========================== ID number validation===========================================     
                var studentIDNumber = student.idNumber.toString();
                if (isNaN(student.idNumber)) {

                    alert("ID number is not valid : It must be a numbers/digits");
                    $scope.passStyle = {"border": "red solid 1px"};
                    return false;
                }

                if (studentIDNumber.length !== 13) {

                    alert("ID number must 13 digits");
                    $scope.passStyle = {"border": "red solid 1px"};
                    return false;
                }

                if (!studentIDNumber.toString().match(numberPattern)) {
                    alert("ID number is not valid : Cannot contain special character");
                    $scope.passStyle = {"border": "red solid 1px"};
                    return false;
                }

                var year = studentIDNumber.slice(0, 2);
                var month = studentIDNumber.slice(2, 4);
                var day = studentIDNumber.slice(4, 6);

                var DOB = new Date();
                var currentYear = new Date().getFullYear().toString();

                if (year > Number(currentYear.slice(2, 4))) {
                    DOB.setFullYear(19 + '' + year, month, day);
                } else {
                    DOB.setFullYear(20 + '' + year, month, day);
                }

                var age = (new Date().getTime() - DOB.getTime()) / 31536000000;

                if (age < 16) {
                    alert("ID number look invalid. Person less than 16 years cannot register as a student");
                    $scope.passStyle = {"border": "red solid 1px"};
                    return false;
                }


                if (Number(month) === 02) {

                    if (Number(day) > 29) {
                        alert("February doesn't have more than 29 days");
                        $scope.passStyle = {"border": "red solid 1px"};
                        return false;
                    }

                }
                if (Number(month) > 12) {
                    alert("Invalid ID number : There are only 12 months in a year");
                    $scope.passStyle = {"border": "red solid 1px"};
                    return false;
                }
                if (Number(month) < 1) {
                    alert("Invalid ID number : month cannot be less than 1");
                    $scope.passStyle = {"border": "red solid 1px"};
                    return false;
                }
                if (Number(day) > 31) {
                    alert("Invalid ID number : day cannot be greater than 31");
                    $scope.passStyle = {"border": "red solid 1px"};
                    return false;
                }
                if (Number(day) < 1) {
                    alert("Invalid ID number : day cannot be less than 1");
                    $scope.passStyle = {"border": "red solid 1px"};
                    return false;
                }
                if (Number(day) < 1) {
                    alert("Invalid ID number : day cannot be less than 1");
                    $scope.passStyle = {"border": "red solid 1px"};
                    return false;
                }

                //=========================== Cell number validation===========================================  

                var telNumber = student.cellnumber;

                if (isNaN(telNumber)) {
                    alert("Cell number is not valid : It must be numbers/digits");
                    $scope.cellStyle = {"border": "red solid 1px"};
                    return false;
                }

                if (!telNumber.toString().match(numberPattern)) {
                    alert("Cell number is not valid : Cannot contain special characters");
                    $scope.cellStyle = {"border": "red solid 1px"};
                    return false;
                }

                if (telNumber.length !== 10) {
                    alert("Cell phone number must be ten digits");
                    $scope.cellStyle = {"border": "red solid 1px"};
                    return false;
                }

                var cellCode = ['076', '082', '071', '079', '072', '078', '081',
                    '073', '060', '065', '064', '061', '083', '084', '061'];

                var telCode = telNumber.slice(0, 3);
                var isCorrect = false;

                for (var i = 0; i < cellCode.length; i++) {
                    if (telCode === cellCode[i]) {
                        isCorrect = true;
                    }
                }

                if (!isCorrect) {
                    alert("Student cell number is not valid. Please check cell phone code");
                    $scope.cellStyle = {"border": "red solid 1px"};
                    return false;
                }



                if (student.studSubject !== undefined) {

                    if (student.studSubject.length < 1) {
                        alert("Please select subjects before submitting the form");
                        return false;
                    }
                }

//  ====================================================== Firstname, Lastname , address ,and course code  ======================================================

                var letters = /^[A-Za-z\s -]+$/;

                if (student.lastname.length > 25) {
                    alert("Last name must be less than or equal to 25 characters long");
                    $scope.surnameStyle = {"border": "red solid 1px"};
                    return false;
                }

                if (!student.lastname.match(letters)) {
                    alert("Last name is invalid. Must contain letters only");
                    $scope.surnameStyle = {"border": "red solid 1px"};
                    return false;
                }

                if (student.firstname.length > 20) {
                    alert("First name is too long");
                    $scope.nameStyle = {"border": "red solid 1px"};
                    return false;
                }

                if (!student.firstname.match(letters)) {
                    alert("First name is invalid. Must contain letters only");
                    $scope.nameStyle = {"border": "red solid 1px"};
                    return false;
                }

                letters = /^[A-Za-z0-9\s . -]+$/;

                if (!student.address.match(letters)) {

                    alert('Address is invalid. Address must contain letters,numbers and periods only');
                    $scope.addressStyle = {"border": "red solid 1px"};
                    return false;
                }

                if (angular.isNumber(student.address)) {

                    alert('This address is not valid');
                    $scope.addressStyle = {"border": "red solid 1px"};
                    return false;
                }

                if (student.address.length > 40) {
                    alert("Postal address must be less than or equal to 40 characters long");
                    $scope.addressStyle = {"border": "red solid 1px"};
                    return false;
                }

                if (student.courseCode.indexOf('--') > -1) {
                    alert("Invalid course code");
                    $scope.courseCodeStyle = {"border": "red solid 1px"};
                    return false;
                }

                return true;
            }

            $scope.changeStyle = function () {
                $scope.nameStyle = {"border": "red #c0c0c0 solid 1px"};
                $scope.surnameStyle = {"border": "red #c0c0c0 solid 1px"};
                $scope.addressStyle = {"border": "red #c0c0c0 solid 1px"};
                $scope.courseCodeStyle = {"border": "red #c0c0c0 solid 1px"};
            };

            $scope.changePassStyle = function () {

                $scope.passStyle = {"border": "red #c0c0c0 solid 1px"};
            };
            $scope.changeStudNumberStyle = function () {

                $scope.studNumberStyle = {"border": "red #c0c0c0 solid 1px"};
            };
            $scope.changeCellStyle = function () {

                $scope.cellStyle = {"border": "red #c0c0c0 solid 1px"};
            };
            //==============================================================================================================================

            function validateAdminData(admin) {

                var userNum = admin.userNumber.toString();

                var userNumberPattern = /^[0-9]+$/;
                if (!userNum.match(userNumberPattern)) {
                    alert("Staff number is invalid : Please provide the correct one");
                    $scope.studNumberStyle = {"border": "red solid 1px"};
                    return false;
                }
                if (userNum.length !== 9) {
                    alert("Staff number must 9 digits");
                    $scope.studNumberStyle = {"border": "red solid 1px"};
                    return false;
                }

                //  ====================================================== surname, name  ======================================================

                var letters = /^[A-Za-z\s -]+$/;

                var emailPattern = /^[0-9A-Za-z@.]+$/;

                if (admin.surname.length > 25) {
                    alert("Last name must be less than or equal to 25 characters long");
                    $scope.surnameStyle = {"border": "red solid 1px"};
                    return false;
                }

                if (!admin.surname.match(letters)) {
                    alert("Last name is invalid. Must contain letters only");
                    $scope.surnameStyle = {"border": "red solid 1px"};
                    return false;
                }

                if (admin.name.length > 25) {
                    alert("First name must be less than or equal to 25 characters long");
                    $scope.nameStyle = {"border": "red solid 1px"};
                    return false;
                }

                if (!admin.name.match(letters)) {
                    alert("First name is invalid. Must contain letters only");
                    $scope.nameStyle = {"border": "red solid 1px"};
                    return false;
                }
//emailStyle

                if (admin.rePassword) {
                    if (admin.password !== admin.rePassword) {
                        alert("Your passwords do not match");
                        $scope.passStyle = {"border": "red solid 1px"};
                        return false;
                    }
                }
                if (admin.password.length < 5) {
                    alert("Password must at least be five characters long");
                    $scope.passStyle = {"border": "red solid 1px"};
                    return false;
                }
                if (admin.password.length > 15) {
                    alert("Password must be less than or equal to 15 characters long");
                    $scope.passStyle = {"border": "red solid 1px"};
                    return false;
                }

                return true;
            }

            $scope.adminRegister = function (admin) {

                admin.usrRole = "ADMIN";


                if (validateAdminData(admin)) {
                    showSpinner(true);
                    $http.post("/ADMIN/register/" + admin.ssKey, admin).then(function (response) {
                        showSpinner(false);

                        if (response.data.status === "REGISTERED") {
                            $scope.admin = {};
                            $scope.goto("/");
                        }
                        alert(response.data.message);
                    });
                }
            };

            $scope.adminGenerateReport = function (reportData) {
                //alert(JSON.stringify(reportData));

                $rootScope.loginID = sessionStorage.getItem("sessionID");

                if ($rootScope.loginID !== null) {
                    reportData.adminNo = sessionStorage.getItem("lecturerNo");

                    $http.post("/ADMIN/admin/generateReport/" + $rootScope.loginID, reportData).then(function (response) {

                        if (response.data.status === 'OK') {
                            response.data.reportDate = new Date(response.data.reportDate);

                            showDialog(response.data, 'admin-report-modal.html');

                        }
                    });

                } else {
                    $scope.goto("/");
                    alert("It looks like you didn't log in, Please log in to perform this operation");
                }


            };





            $scope.closeAlert = function () {

                $rootScope.message = "";
                $rootScope.failureAlert = false;
                $rootScope.successAlert = false;

            };

            $scope.subType = function (subReportType) {
                $scope.subReportType = subReportType;
            };

            function getFormattedDate(dateVal) {

                if (!isNaN(dateVal)) {

                    var registerDate = new Date(dateVal);

                    var day = registerDate.getDay();
                    var month = registerDate.getMonth() + 1;

                    if (day < 10) {
                        day = '0' + day;
                    }
                    if (month < 10) {
                        month = '0' + month;
                    }

                    dateVal = registerDate.getFullYear() + "-" + month + "-" + day;
                }
                return dateVal;
            }
            function parseSubjectsArray(sData) {
                $scope.subjects = [];

                var jsonObj = JSON.parse(sData);

                for (var i = 0, max = jsonObj.length; i < max; i++) {

                    var subject = {
                        subjectCode: jsonObj[i].subjectCode,
                        courseCode: jsonObj[i].courseCode,
                        subjectDesc: jsonObj[i].subjectDesc
                    };
                    $scope.subjects.push(subject);
                }

            }

            function parseAttendingStudentsData(asData) {

                alert(JSON.stringify(asData));
                var asJSONData = JSON.parse(asData);

                for (var i = 0, max = asJSONData.length; i < max; i++) {
                    let eachStud = asJSONData[i];
                    let index = $rootScope.attendingStudents.indexOf(eachStud);
                    if (index < 0) {
                        var student = {
                            id: eachStud.id,
                            studentNumber: eachStud.studentNumber,
                            fullName: eachStud.lastname + " " + $rootScope.eachStud.firstname,
                            status: eachStud.status
                        };
                        alert(JSON.stringify(student));
                        $rootScope.attendingStudents.push(student);
                    }
                    alert(JSON.stringify(student));
                }

            }

            function showAlert(message) {

                var instance = window.setInterval(function () {
                    alert(message);
                    window.clearInterval(instance);
                }, 1200);
            }

            $scope.viewStudentDetails = function (student) {


                if ($rootScope.loginID !== null) {
                    showSpinner(true);
                    $http.get("/TUT/subject/findStudentSubjects/" + student.studentNumber + "/" + $rootScope.loginID).then(function (response) {
                        showSpinner(false);

                        if (response.data.status === "OK") {
                            student.studentSubjects = response.data.studentSubjects;
                            showDialog(student, 'viewStudentDetails.html');
                        }
                    });

                } else {
                    $scope.goto("/");
                    alert("It looks like you didn't log in, Please log in to perform this operation");
                }

            };

            $scope.searchOn = false;
            $scope.searchNotOn = true;

            $scope.search = function (allStudents, query) {

                var results = [];

                if (query.length < 1) {
                    $scope.searchResults = results;
                    $scope.searchOn = false;
                    $scope.searchNotOn = true;
                    $scope.notFound = false;
                    ///$rootScope.attendingStudents = sessionStorage.getItem('onRegisterStudents');
                } else {

                    results = allStudents.filter(function (student) {
                        if (student.studentNumber.toString().toLowerCase().search(query.toString().toLowerCase()) > -1) {
                            return student;
                        }

                    });

                    if (results.length > 0) {
                        $scope.searchResults = results;

                        $scope.searchOn = true;
                        $scope.searchNotOn = false;
                        $scope.notFound = false;
                    }
                    if (results.length <= 0) {
                        $scope.searchResults = results;
                        $scope.notFound = true;
                        $scope.searchOn = true;
                        $scope.searchNotOn = false;
                    } else {
                        $scope.notFound = false;
                        $scope.searchOn = false;
                        $scope.searchNotOn = true;
                        $scope.searchResults = results;
                    }
                }

            };


            $scope.searchLecturer = function (allLecturers, query) {

                var results = [];

                if (query.length < 1) {
                    $scope.searchResults = results;
                    $scope.searchOn = false;
                    $scope.searchNotOn = true;
                    $scope.notFound = false;
                } else {

                    results = allLecturers.filter(function (lecturer) {
                        if (lecturer.userNumber.toString().toLowerCase().search(query.toString().toLowerCase()) > -1) {
                            return lecturer;
                        }

                    });

                    if (results.length > 0) {
                        $scope.searchResults = results;

                        $scope.searchOn = true;
                        $scope.searchNotOn = false;
                        $scope.notFound = false;
                    }
                    if (results.length <= 0) {
                        $scope.searchResults = results;
                        $scope.notFound = true;
                        $scope.searchOn = true;
                        $scope.searchNotOn = false;
                    } else {
                        $scope.notFound = false;
                        $scope.searchOn = false;
                        $scope.searchNotOn = true;
                        $scope.searchResults = results;
                    }
                }


            };

            function showDialog(reportData, templateName) {
                var parentEl = angular.element(document.body);
                $mdDialog.show({
                    parent: parentEl,
                    templateUrl: templateName,
                    locals: {
                        reportData: reportData
                    },
                    controller: DialogController

                });
                function DialogController($scope, $mdDialog, reportData) {
                    $scope.reportData = reportData;
                    $scope.cancel = function () {
                        $mdDialog.hide();
                    };
                    $scope.printReport = function (data, url) {

                        if ($rootScope.loginID !== null) {

                            url += "/" + $rootScope.loginID;
                            showSpinner(true);
                            $http.post(url, data).then(function (response) {
                                showSpinner(false);
                                if (response.data.status === "OK") {
                                    $mdDialog.hide();
                                }

                                alert(response.data.message);

                            });
                        } else {
                            $scope.goto("/");
                            alert("It looks like you didn't log in, Please log in to perform this operation");
                        }

                    };

                    function validate(userNumber) {

                        var pattern = /^[0-9]+$/;

                        if (!userNumber.toString().match(pattern)) {
                            alert('Incorrect admin/lecturer number. Please enter the correct one');
                            return false;
                        }

                        if (userNumber.toString().length !== 9) {
                            alert('This number must be 9 digits long');
                            return false;
                        }

                        return true;
                    }

                    $scope.forgotPassord = function (userNumber) {

                        var temp = userNumber;

                        if (validate(userNumber)) {
                            showSpinner(true);
                            $http.get("/TUT/lecturer/forgotPassord/" + userNumber).then(function (response) {
                                showSpinner(false);
                                if (response.data.status === "OK") {
                                    $mdDialog.hide();
                                } else {
                                    $scope.userNumber = temp;
                                }

                                showAlert(response.data.message)

                            });

                            $scope.userNumber = "";
                        }
                        ;

                    };
                }
            }
            ;


            function showSpinner(isOpen) {

                if (isOpen) {
                    $scope.isActivate = isOpen;

                    var parentEl = angular.element(document.body);
                    $mdDialog.show({
                        parent: parentEl,
                        templateUrl: 'spinner.html',
                        hasBackdrop: false

                    });
                }

                if (!isOpen) {
                    $scope.isActivate = isOpen;
                    $mdDialog.hide();
                }
            }
            ;

            $scope.notWeekends = function (date) {
                var day = date.getDay();
                return day !== 0 && day !== 6;
            };


        });















//        app.controller('lecturerController', dateController);
//alert('loaded');
//            function dateController($scope, $mdDialog) {
//                $scope.myDate = new Date();
//
//                $scope.startDate = new Date();
//                $scope.endDate = new Date();
//
//                $scope.minDate = new Date(
//                        $scope.myDate.getFullYear(),
//                        $scope.myDate.getMonth() - 2,
//                        $scope.myDate.getDate());
//
//                $scope.maxDate = new Date(
//                        $scope.myDate.getFullYear(),
//                        $scope.myDate.getMonth() + 2,
//                        $scope.myDate.getDate());
//
//                $scope.showDates = function (startDate, endDate) {
//                    alert(startDate.getTime() + " "+endDate.getTime());
//                    
//                    if(startDate.getTime() < endDate.getTime()){
//                        
//                        //$scope.showCustom();
//                        alert('is valid');
//                        
//                        $scope.closeDialog();
//                    }else{
//                        
//                        
//                        alert('is invalid');
//                    }
//                }
//
//                $scope.yes = function (){
//                    alert('click')
//                }
//
//                $scope.notWeekends = function (date) {
//                    var day = date.getDay();
//                    return day !== 0 && day !== 6;
//                }
//
//                $scope.status = '';
//                $scope.items = [1, 2, 3, 4, 5];
//                $scope.showAlert = function (ev) {
//                    
//                    alert('1111111111');
//                    $mdDialog.show(
//                            $mdDialog.alert()
//                            .parent(angular.element(document.querySelector('#dialogContainer')))
//                            .clickOutsideToClose(true)
//                            .title('TutorialsPoint.com')
//                            .textContent('Welcome to TutorialsPoint.com')
//                            .ariaLabel('Welcome to TutorialsPoint.com')
//                            .ok('Ok!')
//                            .targetEvent(ev)
//                            );
//                };
//
//                $scope.showConfirm = function (event) {
//                    alert('gfgdfgdfgg');
//                    var confirm = $mdDialog.confirm()
//                            .title('Are you sure to delete the record?')
//                            .textContent('Record will be deleted permanently.')
//                            .ariaLabel('TutorialsPoint.com')
//                            .targetEvent(event)
//                            .ok('Yes')
//                            .cancel('No');
//                    $mdDialog.show(confirm).then(function () {
//                        $scope.status = 'Record deleted successfully!';
//                    }, function () {
//                        $scope.status = 'You decided to keep your record.';
//                    });
//                };
//
//                $scope.showCustom = function () {
//                    $mdDialog.show({
//                        clickOutsideToClose: true,
//                        scope: $scope,
//                        preserveScope: true,
//                        templateUrl: 'modal-view.html',
//                        controller: function DialogController($scope, $mdDialog) {
//                            $scope.closeDialog = function () {
//                                $mdDialog.hide();
//                            }
//                        }
//                    });
//                };
//
//            }
//      
