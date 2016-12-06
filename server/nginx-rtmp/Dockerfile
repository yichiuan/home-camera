FROM resin/rpi-raspbian:jessie-20161117

# Versions of Nginx and nginx-rtmp-module to use
ENV NGINX_VERSION nginx-1.11.6
ENV NGINX_RTMP_MODULE_VERSION 1.1.10
ENV OPENSSL_VERSION 1.0.2j

ENV NGINX_TEMP_DIR=/var/lib/nginx

# Install dependencies
RUN apt-get update && \
    apt-get install -y ca-certificates wget make gcc g++ libssl-dev libpcre++-dev libssl1.0.0 libpcre++ libgoogle-perftools-dev && \
    rm -rf /var/lib/apt/lists/*

# Download and decompress Nginx
RUN mkdir -p /tmp/build/nginx && \
    cd /tmp/build/nginx && \
    wget -O ${NGINX_VERSION}.tar.gz https://nginx.org/download/${NGINX_VERSION}.tar.gz && \
    tar -zxf ${NGINX_VERSION}.tar.gz

# Download and decompress RTMP module
RUN mkdir -p /tmp/build/nginx-rtmp-module && \
    cd /tmp/build/nginx-rtmp-module && \
    wget -O nginx-rtmp-module-${NGINX_RTMP_MODULE_VERSION}.tar.gz https://github.com/arut/nginx-rtmp-module/archive/v${NGINX_RTMP_MODULE_VERSION}.tar.gz && \
    tar -zxf nginx-rtmp-module-${NGINX_RTMP_MODULE_VERSION}.tar.gz

# Download and decompress OpenSSL
RUN mkdir -p /tmp/build/openssl && \
    cd /tmp/build/openssl && \
    wget -O openssl-${OPENSSL_VERSION}.tar.gz https://www.openssl.org/source/openssl-${OPENSSL_VERSION}.tar.gz && \
    tar -zxf openssl-${OPENSSL_VERSION}.tar.gz

# Build and install Nginx
# The default puts everything under /usr/local/nginx, so it's needed to change
# it explicitly. Not just for order but to have it in the PATH
RUN cd /tmp/build/nginx/${NGINX_VERSION} && \
    ./configure \
        --prefix=/usr/share/nginx \
        --sbin-path=/usr/sbin/nginx \
        --conf-path=/etc/nginx/nginx.conf \
        --pid-path=/var/run/nginx.pid \
        --lock-path=/var/lock/nginx.lock \
        --error-log-path=/var/log/nginx/error.log \
        --http-log-path=/var/log/nginx/access.log \
        --http-client-body-temp-path=${NGINX_TEMP_DIR}/body \
        --http-proxy-temp-path=${NGINX_TEMP_DIR}/proxy \
        --http-uwsgi-temp-path=${NGINX_TEMP_DIR}/uwsgi \
        --with-pcre-jit \
        --with-http_gunzip_module \
        --with-http_gzip_static_module \
        --with-http_ssl_module \
        --with-http_v2_module \
        --with-threads \
        --with-google_perftools_module \
        --with-openssl=/tmp/build/openssl/openssl-${OPENSSL_VERSION} \
        --add-module=/tmp/build/nginx-rtmp-module/nginx-rtmp-module-${NGINX_RTMP_MODULE_VERSION} && \
    make -j $(getconf _NPROCESSORS_ONLN) && \
    make install && \
    mkdir /etc/nginx/conf && \
    mkdir /etc/nginx/certs && \
    cd / && \
    rm -rf /tmp/build

# forward request and error logs to docker log collector
RUN ln -sf /dev/stdout /var/log/nginx/access.log && \
    ln -sf /dev/stderr /var/log/nginx/error.log

VOLUME ["/etc/nginx/conf", "/etc/nginx/certs", "/var/log/nginx"]

EXPOSE 80 443 1935

CMD ["nginx", "-c", "/etc/nginx/conf/nginx.conf", "-g", "daemon off;"]