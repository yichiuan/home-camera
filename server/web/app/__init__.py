import logging
import os

from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from flask_migrate import Migrate

from config import config

# Flask extensions
db = SQLAlchemy()
migrate = Migrate()


def create_app(config_name=None):

    if config_name is None:
        config_name = os.environ.get('FLACK_CONFIG', 'development')

    app = Flask(__name__)

    app.config.from_object(config[config_name])

    init_logger(app)

    # Initialize flask extensions
    db.init_app(app)
    migrate.init_app(app, db)

    @app.route("/")
    def hello():
        return "Hello World!"

    # register blueprint
    from app import auth
    app.logger.info('Registering authentication blueprint.')
    app.register_blueprint(auth.auth, url_prefix='/auth')

    from app.auth import token_auth

    @app.route("/user/me")
    @token_auth.login_required
    def get_user():
        from flask import g, jsonify
        app.logger.info(g.current_user.to_dict())
        return jsonify(g.current_user.to_dict())

    return app


def init_logger(app):
    if 'LOGGER_LEVEL' in app.config:
        app.logger.setLevel(app.config['LOGGER_LEVEL'])

    stream_handler = logging.StreamHandler()
    stream_handler.setLevel(logging.INFO)
    app.logger.addHandler(stream_handler)
