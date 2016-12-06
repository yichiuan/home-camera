FROM nginx:1.11.5
RUN rm /etc/nginx/nginx.conf
COPY nginx.conf /etc/nginx/
RUN rm /etc/nginx/conf.d/default.conf

VOLUME ["/etc/nginx/conf.d"]