FROM node:20-alpine
WORKDIR /app
COPY package*.json ./
# 解決你之前的依賴衝突
RUN npm install --legacy-peer-deps
COPY . .
EXPOSE 5173
# 讓 Vite 監聽所有 IP，否則 Docker 外部連不進去
CMD ["npm", "run", "dev", "--", "--host"]