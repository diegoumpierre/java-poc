const game = document.getElementById("game");
const player = document.getElementById("player");
const bulletsContainer = document.getElementById("bullets");
const scoreDisplay = document.getElementById("score");
const lifesDisplay = document.getElementById("lifes");
const gameOverText = document.getElementById("gameOverText");

let score = 0;
let lifes = 3;
let gameOver = false;
let bullet = null;
let enemies = [];
let enemyDirection = 1; 
let enemySpeed = 30;
let enemyMoveInterval;
let ufo = null;
let ufoActive = false;


document.addEventListener("keydown", (e) => {
  const left = parseInt(player.style.left);
  if (e.key === "ArrowLeft" && left > 0) {
    player.style.left = `${left - 20}px`;
  }
  if (e.key === "ArrowRight" && left < 560) {
    player.style.left = `${left + 20}px`;
  }
  if (e.key === " " && !bullet && !gameOver) {
    shootBullet();
  }
});

function shootBullet() {
  bullet = document.createElement("div");
  bullet.classList.add("bullet");
  bullet.style.left = `${parseInt(player.style.left) + 30}px`;
  bullet.style.bottom = "30px";
  bulletsContainer.appendChild(bullet);
}

function gridEnemies() {
  const enemiesContainer = document.getElementById("enemies");
  enemiesContainer.innerHTML = "";
  enemies = [];

  for (let row = 0; row < 4; row++) {
    for (let col = 0; col < 10; col++) {
      const enemy = document.createElement("div");
      enemy.classList.add("enemy");
      enemy.style.left = `${col * 50}px`;
      enemy.style.top = `${row * 40}px`;
      enemiesContainer.appendChild(enemy);
      enemies.push(enemy);
    }
  }
}


function moveEnemies() {
  let moveDown = false;

  for (let enemy of enemies) {
    let left = parseInt(enemy.style.left);
    if ((left <= 0 && enemyDirection === -1) || (left >= 565 && enemyDirection === 1)) {
      moveDown = true;
      break;
    }
  }

  for (let enemy of enemies) {
    let left = parseInt(enemy.style.left);
    let top = parseInt(enemy.style.top);
    if (moveDown) {
      enemy.style.top = `${top + 20}px`;
    } else {
      enemy.style.left = `${left + enemyDirection * 10}px`;
    }

    if (top >= 540) {
      endGame();
    }
  }

  if (moveDown) enemyDirection *= -1;
}

function updateBullet() {
  if (!bullet) return;

  let bottom = parseInt(bullet.style.bottom);
  bullet.style.bottom = `${bottom + 10}px`;

  if (bottom > 540) {
    bullet.remove();
    bullet = null;
    return;
  }

  for (let i = 0; i < enemies.length; i++) {
    let e = enemies[i];
    let eLeft = parseInt(e.style.left);
    let eTop = parseInt(e.style.top);
    let bLeft = parseInt(bullet.style.left);
    let bBottom = parseInt(bullet.style.bottom);

    if (
      bLeft >= eLeft &&
      bLeft <= eLeft + 30 &&
      bBottom >= eTop &&
      bBottom <= eTop + 20
    ) {
      e.remove();
      enemies.splice(i, 1);
      bullet.remove();
      bullet = null;
      score += 10;
      scoreDisplay.textContent = score;
      updateEnemySpeed();
      return;
    }
  }

  if (ufo && ufoActive) {
    let ufoLeft = parseInt(ufo.style.left);
    if (
      parseInt(bullet.style.bottom) > 350 &&
      parseInt(bullet.style.left) >= ufoLeft &&
      parseInt(bullet.style.left) <= ufoLeft + 40
    ) {
      ufo.remove();
      ufo = null;
      ufoActive = false;
      bullet.remove();
      bullet = null;
      score += 100;
      scoreDisplay.textContent = score;
    }
  }
}

function updateEnemySpeed() {
  clearInterval(enemyMoveInterval);
  enemySpeed = Math.max(10, 30 - enemies.length); 
  enemyMoveInterval = setInterval(moveEnemies, 500 - (40 - enemySpeed) * 10);
}

function spawnUFO() {
  if (ufoActive || gameOver) return;

  ufo = document.createElement("div");
  ufo.classList.add("enemy");
  ufo.style.top = "0px";
  ufo.style.left = "-50px";
  ufo.style.width = "40px";
  ufo.style.height = "20px";
  ufo.style.backgroundColor = "magenta";
  document.getElementById("game").appendChild(ufo);
  ufoActive = true;

  let pos = -50;
  const interval = setInterval(() => {
    if (!ufo) return clearInterval(interval);
    pos += 5;
    ufo.style.left = `${pos}px`;
    if (pos > 600) {
      ufo.remove();
      ufo = null;
      ufoActive = false;
      clearInterval(interval);
    }
  }, 50);
}


function loseLife() {
  lifes--;
  lifesDisplay.textContent = lifes;
  if (lifes <= 0) {
    endGame();
  }
}

function endGame() {
  gameOver = true;
  clearInterval(enemyMoveInterval);
  gameOverText.classList.remove("hidden");
  enemies.forEach(e => e.remove());
  if (bullet) bullet.remove();
  if (ufo) ufo.remove();
}

function youWin() {
  gameOver = true;
  clearInterval(enemyMoveInterval);
  winText.classList.remove("hidden");
  enemies.forEach(e => e.remove());
  if (bullet) bullet.remove();
  if (ufo) ufo.remove();
}


function gameLoop() {

  if (enemies.length === 0) {
    youWin();
  }

  if (!gameOver) {
    updateBullet();
    requestAnimationFrame(gameLoop);
  }
}

// Start game
player.style.left = "280px";
gridEnemies();
updateEnemySpeed();
gameLoop();

// UFO every 15 seconds
setInterval(spawnUFO, 15000);
