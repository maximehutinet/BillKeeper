FROM node:23.10-alpine AS builder
WORKDIR /usr/src/app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build --configuration=production

FROM nginx:alpine3.21
COPY docker/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /usr/src/app/dist/billkeeper-front/browser /srv/app
