import os

basedir = os.path.abspath(os.path.dirname(__file__))


class Config:
    DEBUG = False
    TESTING = False
    SECRET_KEY = os.environ.get('SECRET_KEY',
                                b'\xb7w\xcf\x7f1\xb6d\x0fN\xe0)\x88\r\xf60\x05~\x1b\xc3\x9b,\x86\x15\x86')

    # token expires in 30 days
    TOKEN_EXPIRES_IN = 60 * 60 * 24 * 30

    # SQLalchemy
    SQLALCHEMY_DATABASE_URI = os.environ.get(
        'DATABASE_URL', 'sqlite:///' + os.path.join(basedir, "tmp", 'db.sqlite'))
    SQLALCHEMY_TRACK_MODIFICATIONS = False


class DevelopmentConfig(Config):
    DEBUG = True
    LOGGER_LEVEL = 'INFO'

    # SQLalchemy
    SQLALCHEMY_DATABASE_URI = os.environ.get(
        'DATABASE_URL_DEV', 'sqlite:///' + os.path.join(basedir, "tmp", 'dev_db.sqlite'))
    SQLALCHEMY_ECHO = True

    # Mail
    SENDGRID_API_KEY = os.environ.get('SENDGRID_API_KEY', '')
    SENDGRID_DEFAULT_FROM = 'admin@flask.example.com'
    SENDGRID_TEMPLATE_ID = "e4e601ef-6729-4cd3-a127-1d7bd23ba451"

    # MongoDB
    MONGO_DBNAME = 'dev'


class ProductionConfig(Config):
    LOGGER_LEVEL = 'INFO'
    SECRET_KEY = 'stock server secret key for production'

    # SQLalchemy
    SQLALCHEMY_DATABASE_URI = os.environ.get('DATABASE_URL', 'mysql://user:password@host/database')

    # MongoDB
    MONGO_HOST = 'prod_host'
    MONGO_DBNAME = 'dbname'


class TestingConfig(Config):
    TESTING = True
    LOGGER_LEVEL = 'DEBUG'

    # SQLalchemy
    # in-memory database, two ways 'sqlite://' or 'sqlite:///:memory:'
    SQLALCHEMY_DATABASE_URI = 'sqlite://'
    # SQLALCHEMY_ECHO = True

    # Mail
    SENDGRID_API_KEY = os.environ.get('SENDGRID_API_KEY', '')
    SENDGRID_TEMPLATE_ID = ""

    # MongoDB
    MONGO_DBNAME = 'test'


config = {
    'development': DevelopmentConfig,
    'production': ProductionConfig,
    'testing': TestingConfig
}
