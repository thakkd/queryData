module.exports = require('./lib/express');
var express = require('express')
var app = express()

/*
 * body-parser is a piece of express middleware that 
 *   reads a form's input and stores it as a javascript
 *   object accessible through `req.body` 
 *
 * 'body-parser' must be installed (via `npm install --save body-parser`)
 * For more info see: https://github.com/expressjs/body-parser
 */
var bodyParser = require('body-parser');

app.use(bodyParser());

app.get('/', function(req, res){
  // The form's action is '/' and its method is 'POST',
  // so the `app.post('/', ...` route will receive the
  // result of our forma
console.time('handler name');

var MongoClient = require('mongodb').MongoClient
    , format = require('util').format;
MongoClient.connect('mongodb://127.0.0.1:27017/queryData', function (err, db) {
    if (err) {
        throw err;
    } else {
        console.log("successfully connected to the database");
    }

    var import1= db.collection('import1');
    import1.find({w0:req.query.q}).sort({score: -1}).limit(5).toArray(function(err, results) {
        //console.dir(results);
	res.send(results);
        // Let's close the db
	db.close();
	});

});


console.timeEnd('handler name');
//var html = '<html><body>Results:' + resultsJSON + '</body></html>';
//  var html = '<html> <script src="http://code.jquery.com/jquery-1.10.1.min.js"></script>' +
//	       '<title>Chitika keypress demo' + req.query.q + '</title>' + 
//	       '<form action="/" method="post">' +
  //             'Start typing your text' +
 //              '<input type="text" name="userName" placeholder="..." />' +
 //              '<br>' +
 //              '<button type="submit">Submit</button>' +
 //           '</form></html>';
               
 // res.send(html);
});

// This route receives the posted form.
// As explained above, usage of 'body-parser' means
// that `req.body` will be filled in with the form elements
app.post('/', function(req, res){
  var userName = req.body.userName;
  var html = 'Hello: ' + userName + '.<br>' +
             '<a href="/">Try again.</a>';
  res.send(html);
});

app.listen(80);
