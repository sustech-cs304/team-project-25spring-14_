import flask
from utils import *

app = flask.Flask(__name__)

@app.route('/rotate')
def hello():
    return 'Hello World!'

if __name__ == '__main__':
    app.run()