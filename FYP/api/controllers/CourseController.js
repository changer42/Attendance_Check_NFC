/**
 * CourseController
 *
 * @description :: Server-side logic for managing Courses
 * @help        :: See http://links.sailsjs.org/docs/controllers
 */

 module.exports = {

 	create: function (req, res) {

 		if(req.method=="POST"&&req.param("Course",null)!=null)
 		{

 			Course.create(req.param("Course")).exec(function(err,model){


                  // Error handling
                  if (err) {

                  	res.send("Error:Sorry!Something went Wrong");

                  }else {
                  	res.send("Successfully Created!");
                    //res.redirect( ‘person/view/’+model.id);

                }
                
                
            });

 		}
 		else
 		{

 			res.render( 'Course/create');
 		}


 	},



 	index: function (req, res) {

 		Course.find().exec(function(err, Courses) {

      //console.log(Courses);

 			res.render( 'Course/index',{'course':Courses});
 			return;

 		});    
 	} ,

 	json: function (req, res)  {

 		Course.find().exec(function(err, Courses) {

 			res.json(Courses);
 			return;

 		});    
 	},
 	view: function (req, res) {

 		var code=req.param("id",null);

 		Course.findOne().where({code:code}).exec(function(err,model){

      //console.log(model);

 			res.render( 'Course/view',{'model':model});     

 		});    


 	},
 	update: function (req, res) {

 		var code=req.param("id",null);

 		Course.findOne().where({code:code}).exec(function(err, model) {

 			if(req.method=="POST"&&req.param("Course",null)!=null)
 			{

 				var p=req.param("Course",null);


 				model.name=p.name;
 				model.code=p.code;
 				model.remark=p.remark;

 				model.save(function(err){

 					if (err) {

 						res.send("Error");

 					}else {

            //console.log(model);

 						res.redirect( 'Course/view/'+model.code);

 					}


 				});

 			}
 			else
 			{

 				res.render( 'Course/update',{'model':model});
 			}


 		});


 	},
 	delete: function (req, res) {
 		
 		var code=req.param("id",null);
 		
 		Course.findOne().where({code:code}).exec(function(err, user) {

 			user.destroy(function(err) {
 				
 				res.redirect( 'Course/index/');
 				
              // record has been removed
          });

 		});
 	}
 		

 };

