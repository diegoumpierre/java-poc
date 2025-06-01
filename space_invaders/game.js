const game = document.getElementById("game");
const player = document.getElementById("player");
const bulletsContainer = document.getElementById("bullets");
const scoreDisplay = document.getElementById("score");
const livesDisplay = document.getElementById("lives");
const gameOverText = document.getElementById("gameOverText");

let score = 0;
let lives = 3;
let gameOver = false;
let bullet = null;
let enemies = [];
let enemyDirection = 1;
let enemySpeed = 30;
let enemyMoveInterval;
let enemyBullets = [];

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
  bullet.style.left = `${parseInt(player.style.left) + 17}px`;
  bullet.style.bottom = "30px";
  bulletsContainer.appendChild(bullet);
}

function spawnEnemies() {
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

function spawnShields() {
  const shieldsContainer = document.getElementById("shields");
  shieldsContainer.innerHTML = "";
  const baseLefts = [100, 250, 400];

  baseLefts.forEach((baseLeft) => {
    for (let row = 0; row < 4; row++) {
      for (let col = 0; col < 6; col++) {
        const block = document.createElement("div");
        block.classList.add("shield-block");
        block.style.left = `${baseLeft + col * 10}px`;
        block.style.bottom = `${100 + row * 10}px`;
        shieldsContainer.appendChild(block);
      }
    }
  });
}

function moveEnemies() {
  let moveDown = false;

  for (let enemy of enemies) {
    let left = parseInt(enemy.style.left);
    if ((left <= 0 && enemyDirection === -1) || (left >= 550 && enemyDirection === 1)) {
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

    if (top >= 340) {
      endGame();
    }
  }

  if (moveDown) enemyDirection *= -1;
}

function updateBullet() {
  if (!bullet) return;

  let bottom = parseInt(bullet.style.bottom);
  bullet.style.bottom = `${bottom + 10}px`;

  if (bottom > 400) {
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

  const blocks = document.querySelectorAll(".shield-block");
  blocks.forEach((block) => {
    const bLeft = parseInt(bullet.style.left);
    const bBottom = parseInt(bullet.style.bottom);
    const sLeft = parseInt(block.style.left);
    const sBottom = parseInt(block.style.bottom);

    if (
      bLeft >= sLeft &&
      bLeft <= sLeft + 10 &&
      bBottom >= sBottom &&
      bBottom <= sBottom + 10
    ) {
      block.remove();
      bullet.remove();
      bullet = null;
    }
  });
}

function updateEnemySpeed() {
  clearInterval(enemyMoveInterval);
  enemySpeed = Math.max(10, 30 - enemies.length);
  enemyMoveInterval = setInterval(moveEnemies, 500 - (40 - enemySpeed) * 10);
}

function enemyShoot() {
  if (enemies.length === 0) return;

  const shooter = enemies[Math.floor(Math.random() * enemies.length)];
  const bullet = document.createElement("div");
  bullet.classList.add("enemy-bullet");
  bullet.style.left = `${parseInt(shooter.style.left) + 13}px`;
  bullet.style.top = `${parseInt(shooter.style.top) + 20}px`;
  document.getElementById("game").appendChild(bullet);
  enemyBullets.push(bullet);
}

function updateEnemyBullets() {
  for (let i = 0; i < enemyBullets.length; i++) {
    const bullet = enemyBullets[i];
    let top = parseInt(bullet.style.top);
    bullet.style.top = `${top + 5}px`;

    if (top > 400) {
      bullet.remove();
      enemyBullets.splice(i, 1);
      i--;
      continue;
    }

    const bLeft = parseInt(bullet.style.left);
    const pLeft = parseInt(player.style.left);
    if (
      top >= 370 &&
      bLeft >= pLeft &&
      bLeft <= pLeft + 40
    ) {
      bullet.remove();
      enemyBullets.splice(i, 1);
      loseLife();
      i--;
      continue;
    }

    const shields = document.querySelectorAll(".shield-block");
    for (let j = 0; j < shields.length; j++) {
      const shield = shields[j];
      const sLeft = parseInt(shield.style.left);
      const sBottom = parseInt(shield.style.bottom);
      if (
        bLeft >= sLeft &&
        bLeft <= sLeft + 10 &&
        top >= sBottom &&
        top <= sBottom + 10
      ) {
        shield.remove();
        bullet.remove();
        enemyBullets.splice(i, 1);
        i--;
        break;
      }
    }
  }
}

function loseLife() {
  lives--;
  livesDisplay.textContent = lives;
  if (lives <= 0) {
    endGame();
  }
}

function endGame() {
  gameOver = true;
  clearInterval(enemyMoveInterval);
  gameOverText.classList.remove("hidden");
  enemies.forEach(e => e.remove());
  if (bullet) bullet.remove();
  enemyBullets.forEach(b => b.remove());
}

function gameLoop() {
  if (!gameOver) {
    updateBullet();
    updateEnemyBullets();
    requestAnimationFrame(gameLoop);
  }
}

player.style.left = "280px";
spawnEnemies();
spawnShields();
updateEnemySpeed();
gameLoop();
setInterval(() => {
  if (!gameOver) enemyShoot();
}, 1000);
