int time = (int) Math.floor((((double) length) * scale * timeScale * 3.6) / (speed * 1000));
int penalty = 0;
if (toSpeed < speed) {
  int penalty1 = penaltySolver.getDecelerationPenalty(speed);
  int penalty2 = penaltySolver.getDecelerationPenalty(toSpeed);
  penalty = penalty1 - penalty2;
}
if (fromSpeed < speed) {
  int penalty1 = penaltySolver.getAccelerationPenalty(fromSpeed);
  int penalty2 = penaltySolver.getAccelerationPenalty(speed);
  penalty = penalty + penalty2 - penalty1;
}
time = time + (int)Math.round(penalty * 0.18d * timeScale);
time = time + addedTime;
time = ((int)((time + 40) / 60)) * 60;
return time;
